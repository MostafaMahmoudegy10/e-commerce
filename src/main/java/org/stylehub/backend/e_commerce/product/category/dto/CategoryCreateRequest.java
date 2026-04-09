package org.stylehub.backend.e_commerce.product.category.dto;

import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.UUID;

public record CategoryCreateRequest(
        String categoryName,
        Character categoryGender,
        String categoryDescription,
        MultipartFile categoryIcon,
        UUID  parentCategoryId
) {
}
