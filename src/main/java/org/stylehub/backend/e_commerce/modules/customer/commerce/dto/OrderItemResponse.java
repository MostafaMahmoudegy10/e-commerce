package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID productItemId,
        UUID productId,
        String productNameEn,
        String color,
        String sizeName,
        Integer quantity,
        BigDecimal price,
        BigDecimal totalPrice
) {
}
