package org.stylehub.backend.e_commerce.customer.profile.dto.category;

public record CategoryNameDto(
        String categoryNameEn,
        String categoryNameAr,
        Boolean hasChildren
) {
}
