package org.stylehub.backend.e_commerce.customer.dto.order;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record CheckoutResponse(
        UUID orderId,
        String orderNumber,
        String brandName,
        BigDecimal totalPrice,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        String message
) {
}
