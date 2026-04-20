package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto;

import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.product.product_item.size.Size;

import java.util.List;

public record ProductItemPatchRequest(
        String color,
        Integer stock,
        String sku,
        List<SizeDtoReqRes> size,
        List<MultipartFile> images
) {
}
