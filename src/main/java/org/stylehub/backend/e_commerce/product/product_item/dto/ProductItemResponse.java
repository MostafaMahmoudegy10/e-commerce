package org.stylehub.backend.e_commerce.product.product_item.dto;

import java.util.List;
import java.util.UUID;

public record ProductItemResponse(
        UUID productItemId,
        UUID productId,
        String productNameEn,
        String color,
        String sku,
        List<ProductItemSizeRequest> sizeList,
        List<String> imageUrls
) {
}
