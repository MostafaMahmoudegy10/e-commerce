package org.stylehub.backend.e_commerce.modules.catalog.category.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CategoryCreateRequest(
        String categoryName,
        Character categoryGender,
        String categoryDescription,
        MultipartFile categoryIcon,
        UUID  parentCategoryId
) {
}
