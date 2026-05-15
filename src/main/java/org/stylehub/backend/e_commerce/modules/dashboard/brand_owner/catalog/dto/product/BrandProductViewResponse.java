package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product;

import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BrandProductViewResponse(
        UUID productId,
        String productNameEn,
        String productNameAr,
        String thumbnail,
        UUID categoryId,
        String categoryNameEn,
        String categoryNameAr,
        Gender categoryGender,
        Long colorsCount,
        Long variantsCount,
        Long totalStock,
        BigDecimal basePrice,
        Instant createdAt
) {
}
