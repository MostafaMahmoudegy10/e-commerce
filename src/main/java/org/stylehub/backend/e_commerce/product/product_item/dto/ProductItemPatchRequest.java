package org.stylehub.backend.e_commerce.product.product_item.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ProductItemPatchRequest(
        String color,
        String sku,
        List<ProductItemSizeRequest> sizeList,
        List<MultipartFile> imagesOfProductItem
) {
}
