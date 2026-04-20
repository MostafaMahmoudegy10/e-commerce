package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto;

import java.util.List;

public record ProductItemPatchRequest(
        String sku,
        List<SizeDtoReqRes> size
) {
}
