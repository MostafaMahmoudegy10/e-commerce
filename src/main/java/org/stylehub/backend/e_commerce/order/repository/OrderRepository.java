package org.stylehub.backend.e_commerce.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {


    Optional<Order> findOrderByCart_IdAndOrderStatus(UUID cartId, OrderStatus orderStatus);
}
