package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID cartItemId,
        UUID productItemId,
        UUID productId,
        UUID brandId,
        String productNameEn,
        String color,
        String sizeName,
        Integer quantity,
        BigDecimal price,
        BigDecimal totalPrice,
        String thumbnail
) {
}
