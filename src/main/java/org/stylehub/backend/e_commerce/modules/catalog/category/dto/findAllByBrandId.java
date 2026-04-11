package org.stylehub.backend.e_commerce.modules.catalog.category.dto;

import java.util.UUID;

public record findAllByBrandId(
        UUID id,
        String categoryNameEn,
        String categoryNameAr,
        String categoryDescriptionEn,
        String categoryDescriptionAr,
        String categoryIcon
) {
}
