package org.stylehub.backend.e_commerce.product.product_item.dto;

import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.product.product_item.entity.Size;

import java.math.BigDecimal;
import java.util.List;

public record ProductItemDto(
        String color,
        Size size,
        Integer stock,
        BigDecimal price,
        String sku,
        List<MultipartFile> imagesOfProductItem
) {
}
