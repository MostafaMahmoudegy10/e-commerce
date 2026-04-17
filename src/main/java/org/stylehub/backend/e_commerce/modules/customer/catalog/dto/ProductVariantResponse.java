package org.stylehub.backend.e_commerce.modules.customer.catalog.dto;

import java.util.List;
import java.util.UUID;

public record ProductVariantResponse(
        UUID productItemId,
        String color,
        String sku,
        List<String> imageUrls,
        List<ProductVariantSizeResponse> sizes
) {
}
