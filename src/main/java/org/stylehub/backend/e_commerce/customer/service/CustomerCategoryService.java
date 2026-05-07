package org.stylehub.backend.e_commerce.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.customer.profile.dto.category.CategoryNameDto;
import org.stylehub.backend.e_commerce.customer.profile.repository.category.CustomerCategoryRepo;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerCategoryService {

    private final CustomerCategoryRepo categoryRepository;

    public List<CategoryNameDto> findAllParentCategories(UUID brandId, String parentCategoryName) {
        return this.categoryRepository.findAllParentChildCategories(String.valueOf(brandId),parentCategoryName);
    }


}
