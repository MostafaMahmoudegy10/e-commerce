package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto;

import java.math.BigDecimal;

public record BrandDashboardSummaryResponse(
        long productsCount,
        long categoriesCount,
        long ordersCount,
        BigDecimal revenue
) {
}
