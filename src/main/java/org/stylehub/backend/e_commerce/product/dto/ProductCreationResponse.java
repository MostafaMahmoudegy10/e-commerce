package org.stylehub.backend.e_commerce.product.dto;


import java.util.UUID;

public record ProductCreationResponse(
        UUID productId,
        String productNameEn,
        String productNameAr,
        String thumbnail,
        String categoryNameEn
) {
}
