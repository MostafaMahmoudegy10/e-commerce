package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto;

import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.product.product_item.size.Size;

import java.util.List;

public record ProductItemCreateRequest(
        String color,
        String sku,
        String colorCode,
        List<Size>sizeAndStockList,
        List<MultipartFile>productItemImages
) {
}
