package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product.BrandProductFilterRequest;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BrandCatalogProductQueryRepositoryImpl implements BrandCatalogProductQueryRepository {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Page<Product> findBrandProducts(String externalId, BrandProductFilterRequest filter, Pageable pageable) {
        try (EntityManager entityManager = this.entityManagerFactory.createEntityManager()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Product> query = cb.createQuery(Product.class);
            Root<Product> root = query.from(Product.class);

            root.fetch("category", JoinType.INNER);

            Join<Product, Brand> brandJoin = root.join("brand", JoinType.INNER);
            Join<Brand, User> brandUserJoin = brandJoin.join("user", JoinType.INNER);
            Join<Product, Category> categoryJoin = root.join("category", JoinType.INNER);

            query.select(root).distinct(true);
            query.where(buildPredicates(cb, root, brandUserJoin, categoryJoin, externalId, filter));
            query.orderBy(cb.desc(root.get("creationDate")));

            List<Product> items = entityManager.createQuery(query)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Product> countRoot = countQuery.from(Product.class);
            Join<Product, Brand> countBrandJoin = countRoot.join("brand", JoinType.INNER);
            Join<Brand, User> countBrandUserJoin = countBrandJoin.join("user", JoinType.INNER);
            Join<Product, Category> countCategoryJoin = countRoot.join("category", JoinType.INNER);

            countQuery.select(cb.countDistinct(countRoot));
            countQuery.where(buildPredicates(cb, countRoot, countBrandUserJoin, countCategoryJoin, externalId, filter));

            long total = entityManager.createQuery(countQuery).getSingleResult();
            return new PageImpl<>(items, pageable, total);
        }
    }

    private Predicate[] buildPredicates(
            CriteriaBuilder cb,
            Root<Product> root,
            Join<Brand, User> brandUserJoin,
            Join<Product, Category> categoryJoin,
            String externalId,
            BrandProductFilterRequest filter
    ) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(brandUserJoin.get("externalUserId"), externalId));

        if (filter != null && filter.categoryId() != null) {
            predicates.add(cb.equal(categoryJoin.get("id"), filter.categoryId()));
        }

        if (filter != null && filter.gender() != null) {
            predicates.add(cb.equal(categoryJoin.get("categoryGender"), filter.gender()));
        }

        if (filter != null && filter.search() != null && !filter.search().isBlank()) {
            String normalized = "%" + filter.search().trim().toLowerCase() + "%";

            predicates.add(
                    cb.or(
                            cb.like(cb.lower(root.get("productNameEn")), normalized),
                            cb.like(cb.lower(root.get("productNameAr")), normalized),
                            cb.like(cb.lower(categoryJoin.get("categoryNameEn")), normalized),
                            cb.like(cb.lower(categoryJoin.get("categoryNameAr")), normalized)
                    )
            );
        }

        return predicates.toArray(Predicate[]::new);
    }
}
