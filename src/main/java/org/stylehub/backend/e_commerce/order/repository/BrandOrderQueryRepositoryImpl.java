package org.stylehub.backend.e_commerce.order.repository;

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
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto.BrandOrderFilterRequest;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BrandOrderQueryRepositoryImpl implements BrandOrderQueryRepository {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Page<Order> findBrandOrders(String externalId, BrandOrderFilterRequest filter, Pageable pageable) {
        try (EntityManager entityManager = this.entityManagerFactory.createEntityManager()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Order> query = cb.createQuery(Order.class);
            Root<Order> root = query.from(Order.class);

            Fetch<Order, Brand> brandFetch = root.fetch("brand", JoinType.INNER);
            brandFetch.fetch("user", JoinType.INNER);
            Fetch<Order, CustomerProfile> customerFetch = root.fetch("customer", JoinType.INNER);
            customerFetch.fetch("user", JoinType.INNER);

            Join<Order, Brand> brandJoin = root.join("brand", JoinType.INNER);
            Join<Brand, User> brandUserJoin = brandJoin.join("user", JoinType.INNER);
            Join<Order, CustomerProfile> customerJoin = root.join("customer", JoinType.INNER);

            query.select(root).distinct(true);
            query.where(buildPredicates(cb, root, brandUserJoin, customerJoin, externalId, filter));
            query.orderBy(cb.desc(root.get("createdAt")));

            List<Order> items = entityManager.createQuery(query)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Order> countRoot = countQuery.from(Order.class);
            Join<Order, Brand> countBrandJoin = countRoot.join("brand", JoinType.INNER);
            Join<Brand, User> countBrandUserJoin = countBrandJoin.join("user", JoinType.INNER);
            Join<Order, CustomerProfile> countCustomerJoin = countRoot.join("customer", JoinType.INNER);

            countQuery.select(cb.countDistinct(countRoot));
            countQuery.where(buildPredicates(cb, countRoot, countBrandUserJoin, countCustomerJoin, externalId, filter));

            long total = entityManager.createQuery(countQuery).getSingleResult();

            return new PageImpl<>(items, pageable, total);
        }
    }

    private Predicate[] buildPredicates(
            CriteriaBuilder cb,
            Root<Order> root,
            Join<Brand, User> brandUserJoin,
            Join<Order, CustomerProfile> customerJoin,
            String externalId,
            BrandOrderFilterRequest filter
    ) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(brandUserJoin.get("externalUserId"), externalId));

        if (filter != null && filter.status() != null) {
            predicates.add(cb.equal(root.get("orderStatus"), filter.status()));
        }

        if (filter != null && filter.search() != null && !filter.search().isBlank()) {
            String normalized = "%" + filter.search().trim().toLowerCase() + "%";

            Predicate byOrderNumber = cb.like(cb.lower(root.get("orderNumber")), normalized);
            Predicate byCustomerEmail = cb.like(cb.lower(customerJoin.get("customerEmail")), normalized);
            Predicate byUsername = cb.like(cb.lower(customerJoin.get("username")), normalized);
            Predicate byCustomerName = cb.like(
                    cb.lower(
                            cb.concat(
                                    cb.concat(customerJoin.get("firstName"), " "),
                                    customerJoin.get("lastName")
                            )
                    ),
                    normalized
            );

            predicates.add(cb.or(
                    byOrderNumber,
                    byCustomerEmail,
                    byUsername,
                    byCustomerName
            ));
        }

        return predicates.toArray(Predicate[]::new);
    }
}
