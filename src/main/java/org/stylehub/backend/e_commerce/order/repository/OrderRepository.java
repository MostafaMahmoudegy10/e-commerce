package org.stylehub.backend.e_commerce.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.order.entity.Order;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
            select o
            from Order o
            join o.brand b
            join b.user u
            where o.id = :orderId
              and u.externalUserId = :externalUserId
            """)
    Optional<Order> findByIdAndBrand_User_ExternalUserId(UUID orderId, String externalUserId);

    @Query("""
            select distinct o
            from Order o
            join o.brand b
            join b.user u
            where u.externalUserId = :externalUserId
              and (
                    (o.createdAt >= :startDate and o.createdAt < :endDate)
                 or (o.paidAt is not null and o.paidAt >= :startDate and o.paidAt < :endDate)
                 or (o.shippedAt is not null and o.shippedAt >= :startDate and o.shippedAt < :endDate)
                 or (o.deliveredAt is not null and o.deliveredAt >= :startDate and o.deliveredAt < :endDate)
                 or (o.cancelledAt is not null and o.cancelledAt >= :startDate and o.cancelledAt < :endDate)
              )
            """)
    List<Order> findBrandOrdersForCalendar(String externalUserId, Timestamp startDate, Timestamp endDate);

    List<Order> findAllByBrand_User_ExternalUserIdOrderByCreatedAtDesc(String externalUserId);

    List<Order> findAllByUser_ExternalUserIdOrderByCreatedAtDesc(String externalUserId);

    Optional<Order> findByIdAndUser_ExternalUserId(UUID orderId, String externalUserId);
}
