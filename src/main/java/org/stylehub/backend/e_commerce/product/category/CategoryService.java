package org.stylehub.backend.e_commerce.product.category;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.product.category.dto.CategoryCreateRequest;
import org.stylehub.backend.e_commerce.product.category.dto.CategoryResponse;
import org.stylehub.backend.e_commerce.product.category.dto.FindAllCategoryResponse;
import org.stylehub.backend.e_commerce.product.category.entity.Category;
import org.stylehub.backend.e_commerce.product.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryResponse addNewCategory(CategoryCreateRequest request) {
        // check if name is empty or hav white spaces
        if(request.categoryName()==null ||request.categoryName().isBlank()){
            throw new IllegalArgumentException("categoryName is null or empty");
        }
        // check if category is existed before or not
        if(categoryRepository.existsByCategoryNameIgnoreCase(request.categoryName())){
            throw new IllegalArgumentException("categoryName is exist");
        }
        Category category = new Category();
        category.setCategoryName(request.categoryName());
        category.setCategoryGender(Gender.fromCode(request.categoryGender()));
        category.setCategoryDescription(request.categoryDescription());
        // check if he has a parent category
        if(request.parentCategoryId()!=null){
            Category parent= this.categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(()->new IllegalArgumentException("Parent category not found"));
            category.setParentCategory(parent);
        }
        Category savedCategory=this.categoryRepository.save(category);
        return toResponse(savedCategory);
    }
    public Map<String,Object> findAllCategories(Pageable pageable){
        //first check if there is categories
        if(this.categoryRepository.count()==0){
            throw new IllegalArgumentException("there is no category found please add category first and try again !");
        }
        Page<FindAllCategoryResponse> categoryPage = this.categoryRepository.findAllPageableCategory(pageable);
        return (
                Map.of(
                        "content",categoryPage.getContent(),
                        "totalElements",categoryPage.getTotalElements())
                );
    }
    private CategoryResponse toResponse(Category category){
        UUID parentId= category.getParentCategory()!=null?
                category.getParentCategory().getId():null;
        return new CategoryResponse(
                category.getId(),
                category.getCategoryName(),
                category.getCategoryGender(),
                parentId
        );
    }
}
