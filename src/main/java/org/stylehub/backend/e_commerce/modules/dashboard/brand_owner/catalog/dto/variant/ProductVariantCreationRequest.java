package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant;

import java.math.BigDecimal;

public record ProductVariantCreationRequest(
        String size,
        String sku,
        BigDecimal price,
        Integer stock
) {
}
