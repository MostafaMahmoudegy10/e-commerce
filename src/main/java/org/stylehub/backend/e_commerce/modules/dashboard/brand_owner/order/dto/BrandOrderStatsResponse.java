package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import java.math.BigDecimal;

public record BrandOrderStatsResponse(
        BigDecimal revenue,
        long totalOrders,
        long pendingOrders,
        long deliveredOrders
) {
}
