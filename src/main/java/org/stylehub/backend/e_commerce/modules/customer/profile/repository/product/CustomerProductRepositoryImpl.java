package org.stylehub.backend.e_commerce.modules.customer.profile.repository.product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.FindAllProductFilterRequestDto;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.FindAllProductFilterResponseDto;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CustomerProductRepositoryImpl implements CustomerProductRepository {

    private final EntityManagerFactory  entityManagerFactory;

    private final Logger logger= LoggerFactory.getLogger(CustomerProductRepositoryImpl.class);

    @Override
    public Map<String, Object> findAllProductWithFilter(FindAllProductFilterRequestDto dtoRequest, Pageable pageable) {
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

            if(dtoRequest.minPrice()!=null ) {
                countPredicate=cb.and(countPredicate,
                        countCB.greaterThanOrEqualTo(countRootProduct.get("price"), dtoRequest.minPrice()));
            }
            if(dtoRequest.maxPrice()!=null ) {
                countPredicate=cb.and(countPredicate,
                        countCB.lessThanOrEqualTo(countRootProduct.get("price"), dtoRequest.maxPrice()));
            }
            if(dtoRequest.categoryName()!=null ) {
                countPredicate=cb.and(countPredicate,
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

    private EntityManager getEntityManager() {
        return this.entityManagerFactory.createEntityManager();
    }
}
