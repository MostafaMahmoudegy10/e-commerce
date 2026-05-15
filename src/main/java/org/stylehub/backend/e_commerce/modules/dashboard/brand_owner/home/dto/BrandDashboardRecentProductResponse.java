package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BrandDashboardRecentProductResponse(
        UUID productId,
        String productNameEn,
        String productNameAr,
        String categoryNameEn,
        String categoryNameAr,
        BigDecimal price,
        long totalStock,
        Instant createdAt,
        String thumbnail
) {
}
