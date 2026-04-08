package org.stylehub.backend.e_commerce.product.product_item.dto;

import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.product.product_item.size.Size;

import java.math.BigDecimal;
import java.util.List;

public record ProductItemDto(
        String color,
        List<Size> size,
        Integer stock,
        BigDecimal price,
        String sku,
        List<MultipartFile> imagesOfProductItem
) {
}
