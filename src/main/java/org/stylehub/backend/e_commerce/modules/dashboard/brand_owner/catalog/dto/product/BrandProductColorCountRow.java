package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product;

import java.util.UUID;

public record BrandProductColorCountRow(
        UUID productId,
        Long colorsCount
) {
}
