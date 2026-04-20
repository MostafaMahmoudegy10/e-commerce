package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto;

import java.util.UUID;

public record SizeDtoReqRes(
        UUID id,
        String sizeName,
        Integer stock
) {
}
