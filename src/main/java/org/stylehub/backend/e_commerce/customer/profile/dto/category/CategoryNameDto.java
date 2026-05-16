package org.stylehub.backend.e_commerce.customer.profile.dto.category;

import java.util.UUID;

public record CategoryNameDto(
        UUID categoryId,
        String categoryNameEn,
        String categoryNameAr,
        Boolean hasChildren
) {
}
