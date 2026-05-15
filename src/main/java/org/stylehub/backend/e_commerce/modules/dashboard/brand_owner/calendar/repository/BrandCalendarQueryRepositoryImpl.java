package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.user.entity.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BrandCalendarQueryRepositoryImpl implements BrandCalendarQueryRepository {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public List<Order> findOrdersTouchingWindow(String externalId, Instant fromInclusive, Instant toExclusive) {
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

            Predicate createdInWindow = timestampInWindow(
                    cb,
                    root.<Timestamp>get("createdAt"),
                    Timestamp.from(fromInclusive),
                    Timestamp.from(toExclusive)
            );
            Predicate shippedInWindow = instantInWindow(cb, root.<Instant>get("shippedAt"), fromInclusive, toExclusive);
            Predicate deliveredInWindow = instantInWindow(cb, root.<Instant>get("deliveredAt"), fromInclusive, toExclusive);
            Predicate cancelledInWindow = instantInWindow(cb, root.<Instant>get("cancelledAt"), fromInclusive, toExclusive);

            query.select(root).distinct(true);
            query.where(
                    cb.equal(brandUserJoin.get("externalUserId"), externalId),
                    cb.or(createdInWindow, shippedInWindow, deliveredInWindow, cancelledInWindow)
            );
            query.orderBy(cb.asc(root.get("createdAt")));

            return entityManager.createQuery(query).getResultList();
        }
    }

    @Override
    public List<Order> findUpcomingOrders(String externalId, int limit) {
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

            query.select(root).distinct(true);
            query.where(
                    cb.equal(brandUserJoin.get("externalUserId"), externalId),
                    root.<OrderStatus>get("orderStatus").in(List.of(OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.SHIPPED))
            );
            query.orderBy(cb.desc(root.get("createdAt")));

            return entityManager.createQuery(query)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    private Predicate timestampInWindow(
            CriteriaBuilder cb,
            Path<Timestamp> path,
            Timestamp fromInclusive,
            Timestamp toExclusive
    ) {
        return cb.and(
                cb.isNotNull(path),
                cb.greaterThanOrEqualTo(path, fromInclusive),
                cb.lessThan(path, toExclusive)
        );
    }

    private Predicate instantInWindow(
            CriteriaBuilder cb,
            Path<Instant> path,
            Instant fromInclusive,
            Instant toExclusive
    ) {
        return cb.and(
                cb.isNotNull(path),
                cb.greaterThanOrEqualTo(path, fromInclusive),
                cb.lessThan(path, toExclusive)
        );
    }
}
