package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.profile.dto;

import org.springframework.web.multipart.MultipartFile;

public record BrandSetupRequest(
        String brandName,
        String brandEmail,
        String description,
        MultipartFile brandImage
) {
}
