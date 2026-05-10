package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

public record FindAllProductForBrand(
        String productNameAr,
        String productNameEn,
        UUID id,
        String thumbnail,
        String categoryNameEn,
        String categoryNameAr,
        Long currentColors,
        BigDecimal basePrice
) {
}
