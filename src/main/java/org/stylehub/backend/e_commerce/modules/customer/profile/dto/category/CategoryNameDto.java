package org.stylehub.backend.e_commerce.modules.customer.profile.dto.category;

public record CategoryNameDto(
        String categoryNameEn,
        String categoryNameAr,
        Boolean hasChildren
) {
}
