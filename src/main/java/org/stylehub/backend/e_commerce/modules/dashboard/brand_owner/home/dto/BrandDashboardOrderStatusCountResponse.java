package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;

public record BrandDashboardOrderStatusCountResponse(
        OrderStatus status,
        long count
) {
}
