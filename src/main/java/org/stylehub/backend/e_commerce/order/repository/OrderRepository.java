package org.stylehub.backend.e_commerce.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {


    Optional<Order> findOrderByCart_IdAndOrderStatus(UUID cartId, OrderStatus orderStatus);
    @Query("""
            select o
            from Order o
            join fetch o.brand b
            join fetch b.user
            join fetch o.customer c
            join fetch c.user
            where o.id = :orderId
              and b.user.externalUserId = :externalId
            """)
    Optional<Order> findByIdAndBrandExternalId(@Param("orderId") UUID orderId, @Param("externalId") String externalId);

    long countByBrand_User_ExternalUserId(String externalId);

    long countByBrand_User_ExternalUserIdAndOrderStatus(String externalId, OrderStatus status);
}
