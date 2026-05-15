package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public record BrandDashboardRecentOrderResponse(
        UUID orderId,
        String orderNumber,
        String customerName,
        String customerEmail,
        BigDecimal totalPrice,
        OrderStatus orderStatus,
        Timestamp createdAt
) {
}
