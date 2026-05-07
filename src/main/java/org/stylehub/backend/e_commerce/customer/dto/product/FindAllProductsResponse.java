package org.stylehub.backend.e_commerce.customer.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

public record FindAllProductsResponse(
        UUID productId,
        String thumbnail,
        String productNameEn,
        String productNameAr,
        String categoryNameEN,
        String categoryNameAr,
        String productDescriptionEn,
        String productDescriptionAr,
        BigDecimal avgRating,
        Long countColorsAvailable,
        Long totalInStock,
        Boolean isFavourite
) {
}
