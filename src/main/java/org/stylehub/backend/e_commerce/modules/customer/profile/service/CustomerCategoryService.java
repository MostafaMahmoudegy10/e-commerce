package org.stylehub.backend.e_commerce.modules.customer.profile.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.catalog.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.category.CategoryNameDto;
import org.stylehub.backend.e_commerce.modules.customer.profile.repository.category.CustomerCategoryRepo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerCategoryService {

    private final CustomerCategoryRepo categoryRepository;

    public List<CategoryNameDto> findAllParentCategories(UUID brandId, String parentCategoryName) {
        return this.categoryRepository.findAllParentChildCategories(String.valueOf(brandId),parentCategoryName);
    }


}
