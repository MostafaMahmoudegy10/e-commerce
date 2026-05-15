package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product;

import java.util.UUID;

public record BrandProductVariantCountRow(
        UUID productId,
        Long variantsCount
) {
}
