package org.stylehub.backend.e_commerce.modules.customer.profile.dto.product;

import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.SizeDtoReqRes;

import java.util.List;
import java.util.UUID;

public record ProductColorOptionDto(
        UUID itemId,
        String colorCode,
        List<String> itemsImages,
        List<SizeDtoReqRes> sizeDtoReqRes
) {
}
