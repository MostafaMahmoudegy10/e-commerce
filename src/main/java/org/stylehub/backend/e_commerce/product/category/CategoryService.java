package org.stylehub.backend.e_commerce.product.category;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.image.dto.UploadResponse;
import org.stylehub.backend.e_commerce.image.service.ImageService;
import org.stylehub.backend.e_commerce.product.category.dto.CategoryCreateRequest;
import org.stylehub.backend.e_commerce.product.category.dto.CategoryResponse;
import org.stylehub.backend.e_commerce.product.category.dto.FindAllCategoryResponse;
import org.stylehub.backend.e_commerce.product.category.entity.Category;
import org.stylehub.backend.e_commerce.product.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoryService {

    private CategoryRepository categoryRepository;

    private ImageService imageService;

    @Transactional
    public CategoryResponse addNewCategory(CategoryCreateRequest request) {
        // check if name is empty or hav white spaces => 1
        if(request.categoryName()==null ||request.categoryName().isBlank()){
            throw new IllegalArgumentException("categoryName is null or empty");
        }
        // check if category is existed before or not => 2
        if(categoryRepository.existsByCategoryNameIgnoreCase(request.categoryName())){
            throw new IllegalArgumentException("categoryName is exist");
        }
        //check if photo is in the request => 3
        if(request.categoryIcon()==null ||request.categoryIcon().isEmpty()){
            throw new IllegalArgumentException("please add an icon for your category");
        }

        // check if he has a parent category ==> 4
        Category parent=null;
        if(request.parentCategoryId()!=null){
            parent= this.categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(()->new IllegalArgumentException("Parent category not found"));
        }

        // handle uploading of image ==> 5
        UploadResponse image=null;
        try {
            image = this.imageService.uploadImage(request.categoryIcon());
        }catch (IOException e){
            e.printStackTrace();
        }

        // create the category
        Category category = new Category();
        category.setCategoryName(request.categoryName());
        category.setCategoryGender(Gender.fromCode(request.categoryGender()));
        category.setCategoryDescription(request.categoryDescription());
        category.setImageUrl(image.imageUrl());
        category.setPublicId(image.publicId());
        category.setParentCategory(parent);

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
