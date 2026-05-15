package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public record BrandOrderListItemResponse(
        UUID orderId,
        String orderNumber,
        String customerName,
        String customerEmail,
        BigDecimal totalPrice,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        Timestamp createdAt
) {
}
