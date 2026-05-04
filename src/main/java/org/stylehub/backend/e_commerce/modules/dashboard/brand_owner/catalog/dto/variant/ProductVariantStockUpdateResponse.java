package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductVariantStockUpdateResponse(
        UUID variantId,
        UUID productId,
        UUID colorId,
        String colorCode,
        String size,
        String sku,
        Integer stock,
        BigDecimal price
) {
}
