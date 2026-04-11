package org.stylehub.backend.e_commerce.modules.catalog.category.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CategoryPatchRequest(
        String categoryNameEn,
        String categoryNameAr,
        String categoryDescriptionEn,
        String categoryDescriptionAr,
        MultipartFile imageIcon,
        UUID parentCategoryId

) {
}
