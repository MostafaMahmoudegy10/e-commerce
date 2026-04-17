package org.stylehub.backend.e_commerce.product.product_item.dto;

public record ProductItemSizeRequest(
        String sizeName,
        Integer stock
) {
}
