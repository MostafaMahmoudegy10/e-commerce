package org.stylehub.backend.e_commerce.modules.customer.catalog.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductDetailsResponse(
        UUID productId,
        UUID brandId,
        String brandName,
        Double brandAverageRating,
        UUID categoryId,
        String categoryNameEn,
        String productNameEn,
        String productNameAr,
        String productDescriptionEn,
        String productDescriptionAr,
        String thumbnail,
        BigDecimal price,
        Double productAverageRating,
        Long reviewCount,
        List<ProductVariantResponse> variants,
        List<ProductReviewResponse> reviews
) {
}
