package org.stylehub.backend.e_commerce.modules.catalog.category.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CategoryCreateRequest(
        String categoryNameEn,
        String categoryNameAr,
        String categoryDescriptionEn,
        String categoryDescriptionAr,
        Character categoryGender,
        MultipartFile categoryIcon,
        UUID  parentCategoryId
) {
}
