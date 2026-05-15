package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BrandOrderItemDetailsResponse(
        UUID orderItemId,
        UUID productId,
        String productNameEn,
        String productNameAr,
        String thumbnail,
        String colorCode,
        String size,
        String sku,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal totalPrice
) {
}
