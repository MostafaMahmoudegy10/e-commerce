package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.category;

import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

public record BrandCategoryFilterRequest(
        String search,
        Gender gender,
        Boolean hasParent
) {
}
