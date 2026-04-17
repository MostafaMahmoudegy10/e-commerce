package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BrandOwnerOrderItemResponse(
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
