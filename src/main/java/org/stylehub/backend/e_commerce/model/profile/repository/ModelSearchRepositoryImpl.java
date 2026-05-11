package org.stylehub.backend.e_commerce.model.profile.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelSearchFilterRequest;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelSearchRow;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfile;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfileAvailableFor;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ModelSearchRepositoryImpl implements ModelSearchRepository {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Page<ModelSearchRow> searchModels(ModelSearchFilterRequest filter, Pageable pageable) {
        try (EntityManager entityManager = this.entityManagerFactory.createEntityManager()) {
            HibernateCriteriaBuilder cb = entityManager.unwrap(org.hibernate.Session.class).getCriteriaBuilder();

            CriteriaQuery<ModelSearchRow> query = cb.createQuery(ModelSearchRow.class);
            Root<ModelProfile> root = query.from(ModelProfile.class);

            Predicate[] predicates = buildPredicates(filter, cb, query, root);

            query.select(cb.construct(
                    ModelSearchRow.class,
                    root.get("id"),
                    root.get("modelName"),
                    root.get("modelEmail"),
                    root.get("city"),
                    root.get("age"),
                    root.get("heightCm"),
                    root.get("weightKg"),
                    root.get("hairColor"),
                    root.get("bodyType"),
                    root.get("skinTone"),
                    root.get("gender"),
                    root.get("ratingAvg"),
                    root.get("ratingCount"),
                    root.get("isAvailable"),
                    cb.nullLiteral(String.class)
            ));

            query.where(predicates);

            if (filter.search() != null) {
                query.orderBy(
                        cb.desc(cb.sql(
                                "ts_rank(search_vector, websearch_to_tsquery('simple', ?))",
                                Float.class,
                                cb.literal(filter.search())
                        )),
                        cb.desc(root.get("ratingAvg")),
                        cb.asc(root.get("id"))
                );
            } else {
                query.orderBy(
                        cb.desc(root.get("ratingAvg")),
                        cb.asc(root.get("id"))
                );
            }

            TypedQuery<ModelSearchRow> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());

            List<ModelSearchRow> items = typedQuery.getResultList();

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<ModelProfile> countRoot = countQuery.from(ModelProfile.class);
            Predicate[] countPredicates = buildPredicates(filter, cb, countQuery, countRoot);

            countQuery.select(cb.countDistinct(countRoot.get("id")));
            countQuery.where(countPredicates);

            long total = entityManager.createQuery(countQuery).getSingleResult();

            return new PageImpl<>(items, pageable, total);
        }
    }

    private Predicate[] buildPredicates(
            ModelSearchFilterRequest filter,
            HibernateCriteriaBuilder cb,
            CriteriaQuery<?> query,
            Root<ModelProfile> root
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.search() != null) {
            predicates.add(cb.isTrue(cb.sql(
                    "search_vector @@ websearch_to_tsquery('simple', ?)",
                    Boolean.class,
                    cb.literal(filter.search())
            )));
        }
        if (filter.minAge() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("age"), filter.minAge()));
        }
        if (filter.maxAge() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("age"), filter.maxAge()));
        }
        if (filter.minHeightCm() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("heightCm"), filter.minHeightCm()));
        }
        if (filter.maxHeightCm() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("heightCm"), filter.maxHeightCm()));
        }
        if (filter.minWeightKg() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("weightKg"), filter.minWeightKg()));
        }
        if (filter.maxWeightKg() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("weightKg"), filter.maxWeightKg()));
        }
        if (filter.isAvailable() != null) {
            predicates.add(cb.equal(root.get("isAvailable"), filter.isAvailable()));
        }
        if (filter.availableFor() != null && !filter.availableFor().isEmpty()) {
            Subquery<Integer> availableForSubquery = query.subquery(Integer.class);
            Root<ModelProfileAvailableFor> availableForRoot = availableForSubquery.from(ModelProfileAvailableFor.class);

            availableForSubquery.select(cb.literal(1));
            availableForSubquery.where(
                    cb.equal(availableForRoot.get("modelProfile").get("id"), root.get("id")),
                    availableForRoot.get("availableFor").in(filter.availableFor())
            );

            predicates.add(cb.exists(availableForSubquery));
        }

        return predicates.toArray(Predicate[]::new);
    }
}
