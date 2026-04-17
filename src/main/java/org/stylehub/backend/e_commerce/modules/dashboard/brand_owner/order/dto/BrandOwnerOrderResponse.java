package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public record BrandOwnerOrderResponse(
        UUID orderId,
        UUID customerId,
        String customerEmail,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        Timestamp createdAt,
        Timestamp paidAt,
        Timestamp shippedAt,
        Timestamp deliveredAt,
        Timestamp cancelledAt
) {
}
