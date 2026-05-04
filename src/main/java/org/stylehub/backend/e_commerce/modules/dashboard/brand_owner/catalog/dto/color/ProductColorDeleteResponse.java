package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.color;

import java.util.UUID;

public record ProductColorDeleteResponse(
        String message,
        UUID productId,
        UUID colorId
) {
}
