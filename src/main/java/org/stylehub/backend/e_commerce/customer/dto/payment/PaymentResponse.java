package org.stylehub.backend.e_commerce.customer.dto.payment;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        UUID orderId,
        String orderNumber,
        PaymentStatus paymentStatus,
        OrderStatus orderStatus,
        String message
) {
}
