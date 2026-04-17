package org.stylehub.backend.e_commerce.order.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.order.item.entity.OrderItem;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    @Query("""
            select (count(oi) > 0)
            from OrderItem oi
            join oi.order o
            join o.user u
            join oi.productItem pi
            join pi.product p
            where u.externalUserId = :externalUserId
              and p.id = :productId
              and o.orderStatus = org.stylehub.backend.e_commerce.order.entity.OrderStatus.DELIVERED
            """)
    boolean existsDeliveredOrderItemByUserAndProduct(String externalUserId, UUID productId);
}
