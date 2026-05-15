package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto;

import java.math.BigDecimal;

public record BrandDashboardGlanceResponse(
        BigDecimal averagePrice,
        long totalStock,
        long pendingOrders
) {
}
