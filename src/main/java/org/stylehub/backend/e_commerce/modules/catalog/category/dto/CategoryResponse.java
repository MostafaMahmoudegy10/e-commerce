package org.stylehub.backend.e_commerce.modules.catalog.category.dto;

import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.UUID;

public record CategoryResponse(
         UUID id,
         String categoryNameEn,
         String categoryDescriptionEn,
         String categoryNameAr,
         String categoryDescriptionAr,
         String imageUrl,
         Gender categoryGender,
         UUID parentCategoryId
) {
}
