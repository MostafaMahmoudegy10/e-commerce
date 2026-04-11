package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.profile.dto;

import java.util.UUID;

public record BrandSetupResponse(
        UUID id,
        String brandName,
        String brandEmail,
        String description,
        String brandImageUrl
) {
}
