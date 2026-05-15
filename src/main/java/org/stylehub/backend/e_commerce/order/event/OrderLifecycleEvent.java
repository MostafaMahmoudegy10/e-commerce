package org.stylehub.backend.e_commerce.order.event;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderLifecycleEvent(
        UUID orderId,
        String orderNumber,
        UUID brandUserId,
        String customerName,
        String customerEmail,
        BigDecimal totalAmount,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        Instant occurredAt
) {
}
