package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant;

import java.util.UUID;

public record ProductVariantCreationResponse (
        UUID productVariantId,
        String sku,
        String colorCode,
        Integer stock,
        String size
){

}
