package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.repository;

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
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotification;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.payment.entity.Payment;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.user.entity.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BrandDashboardHomeQueryRepositoryImpl implements BrandDashboardHomeQueryRepository {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public List<Order> findRecentOrders(String externalId, int limit) {
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
            query.where(cb.equal(brandUserJoin.get("externalUserId"), externalId));
            query.orderBy(cb.desc(root.get("createdAt")));

            return entityManager.createQuery(query)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    @Override
    public List<Order> findOrdersCreatedSince(String externalId, Instant fromInclusive) {
        try (EntityManager entityManager = this.entityManagerFactory.createEntityManager()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Order> query = cb.createQuery(Order.class);
            Root<Order> root = query.from(Order.class);

            Join<Order, Brand> brandJoin = root.join("brand", JoinType.INNER);
            Join<Brand, User> brandUserJoin = brandJoin.join("user", JoinType.INNER);

            query.select(root);
            query.where(
                    cb.equal(brandUserJoin.get("externalUserId"), externalId),
                    cb.greaterThanOrEqualTo(root.<Timestamp>get("createdAt"), Timestamp.from(fromInclusive))
            );
            query.orderBy(cb.asc(root.get("createdAt")));

            return entityManager.createQuery(query).getResultList();
        }
    }

    @Override
    public List<Payment> findPaidPaymentsSince(String externalId, Instant fromInclusive) {
        try (EntityManager entityManager = this.entityManagerFactory.createEntityManager()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Payment> query = cb.createQuery(Payment.class);
            Root<Payment> root = query.from(Payment.class);

            Join<Payment, Order> orderJoin = root.join("order", JoinType.INNER);
            Join<Order, Brand> brandJoin = orderJoin.join("brand", JoinType.INNER);
            Join<Brand, User> brandUserJoin = brandJoin.join("user", JoinType.INNER);

            List<Predicate> predicates = List.of(
                    cb.equal(brandUserJoin.get("externalUserId"), externalId),
                    cb.equal(root.get("paymentStatus"), PaymentStatus.PAID),
                    cb.isNotNull(root.get("paidAt")),
                    cb.greaterThanOrEqualTo(root.<Instant>get("paidAt"), fromInclusive)
            );

            query.select(root);
            query.where(predicates.toArray(Predicate[]::new));
            query.orderBy(cb.asc(root.get("paidAt")));

            return entityManager.createQuery(query).getResultList();
        }
    }

    @Override
    public List<Product> findRecentProducts(String externalId, int limit) {
        try (EntityManager entityManager = this.entityManagerFactory.createEntityManager()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Product> query = cb.createQuery(Product.class);
            Root<Product> root = query.from(Product.class);

            root.fetch("category", JoinType.INNER);

            Join<Product, Brand> brandJoin = root.join("brand", JoinType.INNER);
            Join<Brand, User> brandUserJoin = brandJoin.join("user", JoinType.INNER);

            query.select(root).distinct(true);
            query.where(cb.equal(brandUserJoin.get("externalUserId"), externalId));
            query.orderBy(cb.desc(root.get("creationDate")));

            return entityManager.createQuery(query)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    @Override
    public List<DashboardNotification> findRecentUnreadNotifications(UUID userId, int limit) {
        try (EntityManager entityManager = this.entityManagerFactory.createEntityManager()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<DashboardNotification> query = cb.createQuery(DashboardNotification.class);
            Root<DashboardNotification> root = query.from(DashboardNotification.class);

            query.select(root);
            query.where(
                    cb.equal(root.get("recipientUser").get("id"), userId),
                    cb.isNull(root.get("readAt"))
            );
            query.orderBy(cb.desc(root.get("createdAt")));

            return entityManager.createQuery(query)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }
}
