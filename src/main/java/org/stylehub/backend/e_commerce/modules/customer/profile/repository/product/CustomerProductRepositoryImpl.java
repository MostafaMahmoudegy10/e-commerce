package org.stylehub.backend.e_commerce.modules.customer.profile.repository.product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.CustomerShowProductDetailsDto;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.FindAllProductFilterRequestDto;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.FindAllProductFilterResponseDto;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.ProductColorOptionDto;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.SizeDtoReqRes;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerProductRepositoryImpl implements CustomerProductRepository {

    private final EntityManagerFactory  entityManagerFactory;

    private final Logger logger= LoggerFactory.getLogger(CustomerProductRepositoryImpl.class);

    @Override
    public Map<String, Object> findAllProductWithFilter(FindAllProductFilterRequestDto dtoRequest, Pageable pageable,String brandId) {
        try(EntityManager entityManager=this.getEntityManager()) {
            entityManager.getTransaction().begin();

            CriteriaBuilder cb =  entityManager.getCriteriaBuilder();
            CriteriaQuery<FindAllProductFilterResponseDto>
                    cq=cb.createQuery(FindAllProductFilterResponseDto.class);
            //select from product
            Root<Product> rootProduct=cq.from(Product.class);

           Join<Product, Category> rootProductCategory=
                   rootProduct.join("category", JoinType.INNER);

           // we create a predicate
           Predicate predicate = cb.conjunction();
            predicate = cb.and(
                    predicate,
                    cb.equal(
                            rootProduct.get("brand")
                                    .get("user")
                                    .get("externalUserId"),
                            brandId
                    )
            );
           if(dtoRequest.minPrice()!=null ) {
               predicate=cb.and(predicate,
                       cb.greaterThanOrEqualTo(rootProduct.get("price"), dtoRequest.minPrice()));
           }
           if(dtoRequest.maxPrice()!=null ) {
               predicate=cb.and(predicate,
                       cb.lessThanOrEqualTo(rootProduct.get("price"), dtoRequest.maxPrice()));
           }
           if(dtoRequest.categoryName()!=null ) {
               predicate=cb.and(predicate,
                       cb.equal(rootProductCategory.get("categoryNameEn"), dtoRequest.categoryName())
                       );
           }

           cq.multiselect(
                   rootProduct.get("productNameEn"),
                   rootProduct.get("productNameAr"),
                   rootProductCategory.get("categoryNameEn"),
                   rootProduct.get("thumbnail"),
                   rootProduct.get("price")
           ).where(predicate);

           if(pageable.getSort().isSorted()){
                cq.orderBy(
                        pageable.getSort()
                                .stream()
                                .map(order->{
                                    Path<?>path;
                                    if(order.getProperty().equals("categoryNameEn")){
                                        path=rootProductCategory.get("categoryNameEn");
                                    }else{
                                        path=rootProduct.get(order.getProperty());
                                    }
                                    return  order.isAscending()?cb.asc(path):cb.desc(path);
                                }).toList()
                );
           }

           TypedQuery<FindAllProductFilterResponseDto> query=entityManager.createQuery(cq);
           query.setFirstResult((int)pageable.getOffset());
           query.setMaxResults(pageable.getPageSize());

           List<FindAllProductFilterResponseDto> products = query.getResultList();
           logger.info("Total products found in this page : {}",products.size());

          // now we need a count query
          CriteriaBuilder countCB=entityManager.getCriteriaBuilder();
          CriteriaQuery<Long> countQuery=countCB.createQuery(Long.class);
          Root<Product>countRootProduct=countQuery.from(Product.class);
          Join<Product,Category> countPrdoucCategoryJoin=countRootProduct.join("category", JoinType.INNER);
          Predicate countPredicate=countCB.conjunction();
          countPredicate=countCB.and(countPredicate,
                  countCB.lessThanOrEqualTo(rootProduct.get("brand").get("user").get("externalUserId"), brandId));

            if(dtoRequest.minPrice()!=null ) {
                countPredicate=countCB.and(countPredicate,
                        countCB.greaterThanOrEqualTo(countRootProduct.get("price"), dtoRequest.minPrice()));
            }
            if(dtoRequest.maxPrice()!=null ) {
                countPredicate=countCB.and(countPredicate,
                        countCB.lessThanOrEqualTo(countRootProduct.get("price"), dtoRequest.maxPrice()));
            }
            if(dtoRequest.categoryName()!=null ) {
                countPredicate=countCB.and(countPredicate,
                        countCB.equal(countPrdoucCategoryJoin.get("categoryNameEn"), dtoRequest.categoryName())
                );
            }
            countQuery.select(countCB.count(countRootProduct)).where(countPredicate);

            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

            logger.info("Total elements found in the db : {}",totalElements);

            entityManager.getTransaction().commit();

            Map<String, Object> response = new HashMap<>();
            response.put("content", products);
            response.put("currentPage", pageable.getPageNumber());
            response.put("pageSize", pageable.getPageSize());
            response.put("totalElements", totalElements);
            response.put("totalPages", (int) Math.ceil((double) totalElements / pageable.getPageSize()));

            return response;
        }

    }

    @Override
    public CustomerShowProductDetailsDto showProductDetails(String brandId, UUID productId, UUID itemId) {
        try (EntityManager em = getEntityManager()) {
            Product product = em.createQuery(
                            "SELECT p FROM Product p " +
                                    "WHERE p.id = :productId " +
                                    "AND p.brand.user.externalUserId = :brandId",
                            Product.class)
                    .setParameter("productId", productId)
                    .setParameter("brandId", brandId)
                    .getSingleResult();

            StringBuilder productItemsQueryBuilder = new StringBuilder(
                    "SELECT pi.id, pi.colorCode FROM ProductItem pi " +
                            "WHERE pi.product.id = :productId"
            );
            if (itemId != null) {
                productItemsQueryBuilder.append(" AND pi.id = :itemId");
            }

            var productItemsQuery = em.createQuery(productItemsQueryBuilder.toString(), Object[].class)
                    .setParameter("productId", productId);
            if (itemId != null) {
                productItemsQuery.setParameter("itemId", itemId);
            }

            List<Object[]> itemRows = productItemsQuery.getResultList();
            if (itemRows.isEmpty()) {
                return new CustomerShowProductDetailsDto(
                        product.getId(),
                        product.getProductNameAr(),
                        product.getProductNameEn(),
                        product.getProductDescriptionEn(),
                        product.getProductDescriptionAr(),
                        product.getPrice(),
                        product.getThumbnail(),
                        List.of()
                );
            }

            Map<UUID, ProductColorOptionBuilder> itemOptions = new LinkedHashMap<>();
            for (Object[] itemRow : itemRows) {
                UUID productItemId = (UUID) itemRow[0];
                String colorCode = (String) itemRow[1];
                itemOptions.put(productItemId, new ProductColorOptionBuilder(productItemId, colorCode));
            }

            List<UUID> productItemIds = new ArrayList<>(itemOptions.keySet());

            List<Object[]> imageRows = em.createQuery(
                            "SELECT pii.productItem.id, pii.imageUrl " +
                                    "FROM ProductItemImage pii " +
                                    "WHERE pii.productItem.id IN :productItemIds",
                            Object[].class)
                    .setParameter("productItemIds", productItemIds)
                    .getResultList();

            for (Object[] imageRow : imageRows) {
                UUID productItemId = (UUID) imageRow[0];
                String imageUrl = (String) imageRow[1];
                ProductColorOptionBuilder builder = itemOptions.get(productItemId);
                if (builder != null) {
                    builder.images().add(imageUrl);
                }
            }

            List<Object[]> sizeRows = em.createQuery(
                            "SELECT s.productItem.id, s.id, s.sizeName, s.stock " +
                                    "FROM Size s " +
                                    "WHERE s.productItem.id IN :productItemIds",
                            Object[].class)
                    .setParameter("productItemIds", productItemIds)
                    .getResultList();

            for (Object[] sizeRow : sizeRows) {
                UUID productItemId = (UUID) sizeRow[0];
                UUID sizeId = (UUID) sizeRow[1];
                String sizeName = (String) sizeRow[2];
                Integer stock = (Integer) sizeRow[3];
                ProductColorOptionBuilder builder = itemOptions.get(productItemId);
                if (builder != null) {
                    builder.sizes().add(new SizeDtoReqRes(sizeId, sizeName, stock));
                }
            }

            List<ProductColorOptionDto> colorOptions = itemOptions.values()
                    .stream()
                    .map(ProductColorOptionBuilder::build)
                    .toList();

            return new CustomerShowProductDetailsDto(
                    product.getId(),
                    product.getProductNameAr(),
                    product.getProductNameEn(),
                    product.getProductDescriptionEn(),
                    product.getProductDescriptionAr(),
                    product.getPrice(),
                    product.getThumbnail(),
                    colorOptions
            );
        } catch (NoResultException e) {
            logger.error("No product found for id: {}", productId);
            return null;
        }
    }

    private record ProductColorOptionBuilder(
            UUID itemId,
            String colorCode,
            List<String> images,
            List<SizeDtoReqRes> sizes
    ) {
        private ProductColorOptionBuilder(UUID itemId, String colorCode) {
            this(itemId, colorCode, new ArrayList<>(), new ArrayList<>());
        }

        private ProductColorOptionDto build() {
            return new ProductColorOptionDto(itemId, colorCode, List.copyOf(images), List.copyOf(sizes));
        }
    }

    private EntityManager getEntityManager() {
        return this.entityManagerFactory.createEntityManager();
    }
}
