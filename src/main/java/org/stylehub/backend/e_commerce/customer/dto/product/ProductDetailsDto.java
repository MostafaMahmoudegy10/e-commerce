package org.stylehub.backend.e_commerce.customer.dto.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailsDto(
        String thumbnail,
        String productNameEn,
        String productNameAr,
        String productDescriptionEn,
        String productDescriptionAr,
        List<ColorDetailsDto>colorDetails,
        BigDecimal avgRating
) {
}
