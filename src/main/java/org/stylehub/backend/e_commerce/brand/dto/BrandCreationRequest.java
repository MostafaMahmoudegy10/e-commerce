package org.stylehub.backend.e_commerce.brand.dto;

public record BrandCreationRequest(
        String brandName,
        String bio,
        String phoneNumber,
        String profileImageUrl
) {
}
