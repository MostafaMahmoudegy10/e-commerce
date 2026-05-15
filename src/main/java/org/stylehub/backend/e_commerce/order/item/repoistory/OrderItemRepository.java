package org.stylehub.backend.e_commerce.order.item.repoistory;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.item.entity.OrderItem;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findAllByOrder_Id(UUID id);

    @Query("""
            select oi
            from OrderItem oi
            join fetch oi.variant v
            join fetch v.productColor pc
            join fetch pc.product p
            where oi.order.id = :orderId
            order by oi.id
            """)
    List<OrderItem> findAllWithDetailsByOrderId(@Param("orderId") UUID orderId);

    @Query("""
            select (count(oi.id) > 0)
            from OrderItem oi
            where oi.order.customer.id = :customerId
              and oi.variant.productColor.product.id = :productId
              and oi.order.orderStatus = :orderStatus
            """)
    boolean existsByCustomerIdAndProductIdAndOrderStatus(
            @Param("customerId") UUID customerId,
            @Param("productId") UUID productId,
            @Param("orderStatus") OrderStatus orderStatus
    );

    @Query("""
            select (count(oi.id) > 0)
            from OrderItem oi
            where oi.order.id = :orderId
              and oi.order.customer.id = :customerId
              and oi.variant.productColor.product.id = :productId
              and oi.order.orderStatus = :orderStatus
            """)
    boolean existsByOrderIdAndCustomerIdAndProductIdAndOrderStatus(
            @Param("orderId") UUID orderId,
            @Param("customerId") UUID customerId,
            @Param("productId") UUID productId,
            @Param("orderStatus") OrderStatus orderStatus
    );

    boolean existsByVariant_ProductColor_Product_Id(UUID productId);

    boolean existsByVariant_ProductColor_Id(UUID productColorId);
}
