package org.stylehub.backend.e_commerce.order.item.repoistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.order.item.entity.OrderItem;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findAllByOrder_Id(UUID id);
}
