package org.stylehub.backend.e_commerce.favourite.dto;

import java.util.UUID;

public record WishlistView(
        String customerUserName,
        String brandUserName,
        UUID productId,
        String productNameEn,
        String productNameAr,
        String thumbnail,
        String productDescriptionEn,
        String productDescriptionAr,
        String categoryNameEn,
        String categoryNameAr
) {
}
