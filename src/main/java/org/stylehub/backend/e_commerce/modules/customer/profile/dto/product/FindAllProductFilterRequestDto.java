package org.stylehub.backend.e_commerce.modules.customer.profile.dto.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;

import java.math.BigDecimal;

public record FindAllProductFilterRequestDto(
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String categoryName
) {
}
