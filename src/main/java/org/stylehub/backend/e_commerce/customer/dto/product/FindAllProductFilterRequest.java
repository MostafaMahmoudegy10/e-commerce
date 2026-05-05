package org.stylehub.backend.e_commerce.customer.dto.product;

import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.math.BigDecimal;
import java.util.UUID;

public record FindAllProductFilterRequest(
        UUID categoryId,
        Long minPrice,
        Long maxPrice,
        BigDecimal minRating,
        Character gender
) {
}
