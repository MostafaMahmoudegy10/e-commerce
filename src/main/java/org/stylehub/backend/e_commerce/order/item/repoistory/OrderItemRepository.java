package org.stylehub.backend.e_commerce.order.item.repoistory;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
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
}
