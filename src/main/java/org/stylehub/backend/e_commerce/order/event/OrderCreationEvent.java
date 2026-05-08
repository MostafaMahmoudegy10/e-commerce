package org.stylehub.backend.e_commerce.order.event;

import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreationEvent(
        UUID orderId,
        String orderNumber,
        UUID customerId,
        String customerName,
        String customerEmail,
        UUID brandId,
        String brandName,
        String brandEmail,
        BigDecimal totalAmount,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        Instant occurredAt
) {
}
