package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.category;

import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.UUID;

public record BrandCategoryViewResponse(
        UUID categoryId,
        String categoryNameEn,
        String categoryNameAr,
        String categoryDescriptionEn,
        String categoryDescriptionAr,
        String imageUrl,
        Gender categoryGender,
        UUID parentCategoryId,
        String parentCategoryNameEn,
        String parentCategoryNameAr,
        boolean hasParent
) {
}
