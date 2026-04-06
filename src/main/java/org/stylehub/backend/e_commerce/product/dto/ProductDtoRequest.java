package org.stylehub.backend.e_commerce.product.dto;

import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemDto;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;

import java.util.List;
import java.util.UUID;

public record ProductDtoRequest(
        ProductDto productDto,
        List<ProductItemDto> productItemList,
        UUID productId
) {
}
