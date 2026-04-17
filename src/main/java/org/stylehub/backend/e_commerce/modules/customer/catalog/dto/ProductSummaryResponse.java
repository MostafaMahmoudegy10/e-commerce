package org.stylehub.backend.e_commerce.modules.customer.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryResponse(
        UUID productId,
        UUID brandId,
        String brandName,
        UUID categoryId,
        String categoryNameEn,
        String productNameEn,
        String productNameAr,
        String thumbnail,
        BigDecimal price,
        Double productAverageRating,
        Double brandAverageRating,
        Long reviewCount
) {
}
