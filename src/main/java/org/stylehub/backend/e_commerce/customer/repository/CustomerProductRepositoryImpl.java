package org.stylehub.backend.e_commerce.customer.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.customer.dto.product.FindAllProductFilterRequest;
import org.stylehub.backend.e_commerce.customer.dto.product.FindAllProductsResponse;
import org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.entity.ProductRatingSummary;
import org.stylehub.backend.e_commerce.favourite.entity.Favourite;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerProductRepositoryImpl implements CustomerProductRepository {


    private final EntityManagerFactory  entityManagerFactory;


    @Override
    public PageResponse<FindAllProductsResponse> findAllProductsFilter(
            FindAllProductFilterRequest filter,
            Pageable pageable,
            String brandId,
            UUID customerId
    ) {
        try (EntityManager entityManager = this.getEntityManager()) {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<FindAllProductsResponse> criteriaQuery =
                    cb.createQuery(FindAllProductsResponse.class);

            Root<Product> rootProduct = criteriaQuery.from(Product.class);

            Join<Product, Category> productCategoryJoin =
                    rootProduct.join("category", JoinType.INNER);


            Subquery<Long> countColorPerProductSubQuery =
                    criteriaQuery.subquery(Long.class);

            Root<ProductColor> productColor =
                    countColorPerProductSubQuery.from(ProductColor.class);

            countColorPerProductSubQuery
                    .select(cb.count(productColor.get("id")))
                    .where(cb.equal(
                            productColor.get("product").get("id"),
                            rootProduct.get("id")
                    ));

            Subquery<Long> countProductStockVarianceSubQuery =
                    criteriaQuery.subquery(Long.class);

            Root<ProductVariant> productVariant =
                    countProductStockVarianceSubQuery.from(ProductVariant.class);

            Join<ProductVariant, ProductColor> productVariantColorJoin =
                    productVariant.join("productColor", JoinType.INNER);

            countProductStockVarianceSubQuery
                    .select(cb.coalesce(cb.sumAsLong(productVariant.get("stock")), 0L))
                    .where(cb.equal(
                            productVariantColorJoin.get("product").get("id"),
                            rootProduct.get("id")
                    ));

            Subquery<BigDecimal> minRatingSubQuery =
                    criteriaQuery.subquery(BigDecimal.class);

            Root<ProductRatingSummary> productRatingRoot =
                    minRatingSubQuery.from(ProductRatingSummary.class);

            minRatingSubQuery
                    .select(productRatingRoot.get("avgRating"))
                    .where(cb.equal(
                            productRatingRoot.get("product").get("id"),
                            rootProduct.get("id")
                    ));

            Subquery<Favourite> favouriteSubquery =
                    criteriaQuery.subquery(Favourite.class);

            Root<Favourite> favouriteRoot =
                    favouriteSubquery.from(Favourite.class);

            favouriteSubquery.select(favouriteRoot)
                    .where(
                            cb.equal(
                                    favouriteRoot.get("product").get("id"),
                                    rootProduct.get("id")
                            ),
                            cb.equal(
                                    favouriteRoot.get("customer").get("id"),
                                    customerId
                            )
                    );

            Expression<Boolean> isFavourite =
                    cb.exists(favouriteSubquery);

            Predicate predicate = cb.conjunction();

            predicate= cb.and(predicate,
                    cb.equal(rootProduct.get("brand").get("user").get("externalUserId"), brandId));

            if (filter != null) {
                if (filter.categoryId() != null) {
                    predicate = cb.and(
                            predicate,
                            cb.equal(productCategoryJoin.get("id"), filter.categoryId())
                    );
                }

                if (filter.gender() != null) {
                    predicate = cb.and(
                            predicate,
                            cb.equal(productCategoryJoin.get("categoryGender"), Gender.fromCode(filter.gender()))
                    );
                }

                if (filter.minRating() != null) {
                    predicate = cb.and(
                            predicate,
                            cb.greaterThanOrEqualTo(
                                    cb.coalesce(minRatingSubQuery, BigDecimal.ZERO),
                                    filter.minRating()
                            )
                    );
                }

                if (filter.maxPrice() != null) {
                    predicate = cb.and(
                            predicate,
                            cb.lessThanOrEqualTo(rootProduct.get("price"), filter.maxPrice())
                    );
                }

                if (filter.minPrice() != null) {
                    predicate = cb.and(
                            predicate,
                            cb.greaterThanOrEqualTo(rootProduct.get("price"), filter.minPrice())
                    );
                }
            }

            criteriaQuery.select(
                    cb.construct(
                            FindAllProductsResponse.class,
                            rootProduct.get("id"),
                            rootProduct.get("thumbnail"),
                            rootProduct.get("productNameEn"),
                            rootProduct.get("productNameAr"),
                            productCategoryJoin.get("categoryNameEn"),
                            productCategoryJoin.get("categoryNameAr"),
                            rootProduct.get("productDescriptionEn"),
                            rootProduct.get("productDescriptionAr"),
                            cb.coalesce(minRatingSubQuery, BigDecimal.ZERO),
                            countColorPerProductSubQuery,
                            countProductStockVarianceSubQuery,
                            isFavourite
                    )
            );

            criteriaQuery.where(predicate);
            criteriaQuery.orderBy(cb.desc(rootProduct.get("creationDate")));

            var query = entityManager.createQuery(criteriaQuery);
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());

            List<FindAllProductsResponse> resultList = query.getResultList();

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);

            Root<Product> countRootProduct = countQuery.from(Product.class);

            Join<Product, Category> countCategoryJoin =
                    countRootProduct.join("category", JoinType.INNER);

            Predicate countPredicate = cb.conjunction();

            countPredicate = cb.and(
                    countPredicate,cb.equal(countRootProduct.get("brand")
                            .get("user").get("externalUserId"), brandId)
            );

            if (filter != null) {
                if (filter.categoryId() != null) {
                    countPredicate = cb.and(
                            countPredicate,
                            cb.equal(countCategoryJoin.get("id"), filter.categoryId())
                    );
                }

                if (filter.gender() != null) {
                    countPredicate = cb.and(
                            countPredicate,
                            cb.equal(countCategoryJoin.get("categoryGender"), filter.gender())
                    );
                }

                if (filter.minRating() != null) {
                    Subquery<BigDecimal> countRatingSubQuery =
                            countQuery.subquery(BigDecimal.class);

                    Root<ProductRatingSummary> countRatingRoot =
                            countRatingSubQuery.from(ProductRatingSummary.class);

                    countRatingSubQuery
                            .select(countRatingRoot.get("avgRating"))
                            .where(cb.equal(
                                    countRatingRoot.get("product").get("id"),
                                    countRootProduct.get("id")
                            ));

                    countPredicate = cb.and(
                            countPredicate,
                            cb.greaterThanOrEqualTo(
                                    cb.coalesce(countRatingSubQuery, BigDecimal.ZERO),
                                    filter.minRating()
                            )
                    );
                }

                if (filter.maxPrice() != null) {
                    countPredicate = cb.and(
                            countPredicate,
                            cb.lessThanOrEqualTo(countRootProduct.get("price"), filter.maxPrice())
                    );
                }

                if (filter.minPrice() != null) {
                    countPredicate = cb.and(
                            countPredicate,
                            cb.greaterThanOrEqualTo(countRootProduct.get("price"), filter.minPrice())
                    );
                }
            }

            countQuery.select(cb.countDistinct(countRootProduct.get("id")));
            countQuery.where(countPredicate);

            long totalElements = entityManager
                    .createQuery(countQuery)
                    .getSingleResult();

            int totalPages = (int) Math.ceil(
                    (double) totalElements / pageable.getPageSize()
            );

            return new PageResponse<FindAllProductsResponse>(
                    resultList,
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    totalElements,
                    totalPages,
                    pageable.getPageNumber() + 1 < totalPages,
                    pageable.getPageNumber() > 0
            );
        }
    }

    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
