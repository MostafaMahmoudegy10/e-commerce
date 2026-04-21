package org.stylehub.backend.e_commerce.modules.customer.profile.repository.category;

import org.stylehub.backend.e_commerce.modules.customer.profile.dto.category.CategoryNameDto;

import java.util.List;

public interface CustomerCategoryRepo {
    List<CategoryNameDto> findAllParentChildCategories(String brandId, String parentCategoryName);
}
