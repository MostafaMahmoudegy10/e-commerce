package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto;

import java.util.UUID;

public record BrandDashboardProductStockRow(
        UUID productId,
        long totalStock
) {
}
