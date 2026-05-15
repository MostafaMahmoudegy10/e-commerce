package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant;

import java.math.BigDecimal;
import java.util.UUID;

public record BrandProductVariantViewResponse(
        UUID variantId,
        String size,
        String sku,
        Integer stock,
        BigDecimal priceOverride,
        BigDecimal effectivePrice
) {
}
