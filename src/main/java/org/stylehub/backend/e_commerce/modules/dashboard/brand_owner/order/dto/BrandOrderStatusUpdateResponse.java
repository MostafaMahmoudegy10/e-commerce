package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record BrandOrderStatusUpdateResponse(
        UUID orderId,
        String orderNumber,
        OrderStatus orderStatus,
        Instant shippedAt,
        Instant deliveredAt,
        Instant cancelledAt,
        String message
) {
}
