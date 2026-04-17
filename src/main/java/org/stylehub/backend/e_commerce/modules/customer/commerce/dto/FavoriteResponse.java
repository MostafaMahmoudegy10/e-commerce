package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import java.util.UUID;

public record FavoriteResponse(
        UUID productId,
        UUID brandId,
        String productNameEn,
        String productNameAr,
        String thumbnail
) {
}
