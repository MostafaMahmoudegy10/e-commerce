package org.stylehub.backend.e_commerce.modules.customer.profile.dto.product;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

public record FindAllProductFilterResponseDto(
        String productNameEn,
        String productNameAr,
        String categoryNameEn,
        String thumbnail,
        BigDecimal price
) {
}
