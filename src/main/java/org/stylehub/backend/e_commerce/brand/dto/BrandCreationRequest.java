package org.stylehub.backend.e_commerce.brand.dto;

public record BrandCreationRequest(
        String brandId,
        String brandName,
        String username,
        String bio,
        String websiteUrl
) {
}
