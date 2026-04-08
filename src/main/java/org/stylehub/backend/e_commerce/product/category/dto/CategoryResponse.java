package org.stylehub.backend.e_commerce.product.category.dto;

import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.UUID;

public record CategoryResponse(
         UUID id,
         String categoryName,
         Gender categoryGender,
         UUID parentCategoryId) {
}
