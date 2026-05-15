package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.category.BrandCategoryFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.category.BrandCategoryStatsResponse;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BrandCatalogCategoryQueryRepositoryImpl implements BrandCatalogCategoryQueryRepository {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Page<Category> findBrandCategories(String externalId, BrandCategoryFilterRequest filter, Pageable pageable) {
        try (EntityManager entityManager = this.entityManagerFactory.createEntityManager()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Category> query = cb.createQuery(Category.class);
            Root<Category> root = query.from(Category.class);

            root.fetch("parentCategory", JoinType.LEFT);

            Join<Category, Brand> brandJoin = root.join("brand", JoinType.INNER);
            Join<Brand, User> brandUserJoin = brandJoin.join("user", JoinType.INNER);

            query.select(root).distinct(true);
            query.where(buildPredicates(cb, root, brandUserJoin, externalId, filter));
            query.orderBy(cb.asc(root.get("categoryNameEn")));

            List<Category> items = entityManager.createQuery(query)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Category> countRoot = countQuery.from(Category.class);
            Join<Category, Brand> countBrandJoin = countRoot.join("brand", JoinType.INNER);
            Join<Brand, User> countBrandUserJoin = countBrandJoin.join("user", JoinType.INNER);

            countQuery.select(cb.countDistinct(countRoot));
            countQuery.where(buildPredicates(cb, countRoot, countBrandUserJoin, externalId, filter));

            long total = entityManager.createQuery(countQuery).getSingleResult();
            return new PageImpl<>(items, pageable, total);
        }
    }

    @Override
    public BrandCategoryStatsResponse getBrandCategoryStats(String externalId) {
        try (EntityManager entityManager = this.entityManagerFactory.createEntityManager()) {
            return new BrandCategoryStatsResponse(
                    countCategories(entityManager, externalId, null, null),
                    countCategories(entityManager, externalId, Gender.MALE, null),
                    countCategories(entityManager, externalId, Gender.FEMALE, null),
                    countCategories(entityManager, externalId, null, true),
                    countCategories(entityManager, externalId, null, false)
            );
        }
    }

    private long countCategories(EntityManager entityManager, String externalId, Gender gender, Boolean hasParent) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Category> root = query.from(Category.class);
        Join<Category, Brand> brandJoin = root.join("brand", JoinType.INNER);
        Join<Brand, User> brandUserJoin = brandJoin.join("user", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(brandUserJoin.get("externalUserId"), externalId));

        if (gender != null) {
            predicates.add(cb.equal(root.get("categoryGender"), gender));
        }
        if (hasParent != null) {
            predicates.add(hasParent ? cb.isNotNull(root.get("parentCategory")) : cb.isNull(root.get("parentCategory")));
        }

        query.select(cb.countDistinct(root));
        query.where(predicates.toArray(Predicate[]::new));
        return entityManager.createQuery(query).getSingleResult();
    }

    private Predicate[] buildPredicates(
            CriteriaBuilder cb,
            Root<Category> root,
            Join<Brand, User> brandUserJoin,
            String externalId,
            BrandCategoryFilterRequest filter
    ) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(brandUserJoin.get("externalUserId"), externalId));

        if (filter != null && filter.gender() != null) {
            predicates.add(cb.equal(root.get("categoryGender"), filter.gender()));
        }

        if (filter != null && filter.hasParent() != null) {
            predicates.add(filter.hasParent() ? cb.isNotNull(root.get("parentCategory")) : cb.isNull(root.get("parentCategory")));
        }

        if (filter != null && filter.search() != null && !filter.search().isBlank()) {
            String normalized = "%" + filter.search().trim().toLowerCase() + "%";
            predicates.add(
                    cb.or(
                            cb.like(cb.lower(root.get("categoryNameEn")), normalized),
                            cb.like(cb.lower(root.get("categoryNameAr")), normalized),
                            cb.like(cb.lower(root.get("categoryDescriptionEn")), normalized),
                            cb.like(cb.lower(root.get("categoryDescriptionAr")), normalized)
                    )
            );
        }

        return predicates.toArray(Predicate[]::new);
    }
}
