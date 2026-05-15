package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product;

import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.UUID;

public record BrandProductFilterRequest(
        String search,
        UUID categoryId,
        Gender gender
) {
}
