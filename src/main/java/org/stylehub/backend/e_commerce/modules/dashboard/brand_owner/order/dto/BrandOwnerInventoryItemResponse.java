package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BrandOwnerInventoryItemResponse(
        UUID productItemId,
        UUID productId,
        String productNameEn,
        String color,
        String sku,
        BigDecimal productPrice,
        Integer totalStock,
        Boolean lowStock,
        List<BrandOwnerInventorySizeResponse> sizes
) {
}
