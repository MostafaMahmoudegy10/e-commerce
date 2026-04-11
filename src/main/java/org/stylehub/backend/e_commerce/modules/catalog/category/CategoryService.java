package org.stylehub.backend.e_commerce.modules.catalog.category;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.repository.BrandRepository;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.*;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.modules.catalog.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoryService {

    private CategoryRepository categoryRepository;
    private BrandRepository brandRepository;
    private CurrentUserProvider currentUserProvider;
    private ImageService imageService;

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);


    @Transactional
    public CategoryResponse addNewCategory(CategoryCreateRequest request) {
        validateCategoryCreateRequest(request);

        String externalId= this.getCurrentBrand();

        // check if the category requested is present or not
        if(externalId!=null){
           boolean exists = this.categoryRepository
                .existsByCategoryNameEnAndBrand_UserExternalUserId(request.categoryNameEn(),externalId);
           if(exists){
               throw new IllegalArgumentException("categoryName for your brand already exists");
           }
        }
        Category parentCategory=null;
        if(request.parentCategoryId()!=null){
            parentCategory =
                    this.categoryRepository.findByIdAndBrand_User_ExternalUserId(request.parentCategoryId(),
                            externalId)
                            .orElseThrow(()-> new IllegalArgumentException("Category You Requested Not Present " +
                                    "For Your Brand Please Add It First And Try Again "));
        }

        UploadResponse image = this.imageService.uploadImage(request.categoryIcon());

       Brand brand = this.brandRepository.findByUser_ExternalUserId(externalId).orElseThrow(
                ()->new IllegalArgumentException("you need to complete yur profile"));

        Category category = new Category();
        category.setBrand(brand);
        category.setCategoryNameEn(request.categoryNameEn());
        category.setCategoryNameAr(request.categoryNameAr());
        category.setCategoryDescriptionAr(request.categoryDescriptionAr());
        category.setCategoryDescriptionEn(request.categoryDescriptionEn());
        category.setParentCategory(parentCategory);
        category.setCategoryGender(Gender.fromCode(request.categoryGender()));
        category.setImageUrl(image.imageUrl());
        category.setPublicId(image.publicId());

        Category savedCategory=this.categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    public Map<String,Object> findAllBrandCategories(Pageable pageable, String brandId){
        // first get owner brand id
        Page<findAllByBrandId> categoryPage
                =this.categoryRepository.findAllByBrand_Id(brandId,pageable);
        return mapPaginatedResponse(categoryPage);
    }

    @Transactional
    public void deleteCategoryOfBrand(UUID categoryId){
       String externalUserId= this.getCurrentBrand();
       Category category=
               this.categoryRepository.findByIdAndBrand_User_ExternalUserId(categoryId,externalUserId)
               .orElseThrow(()->new IllegalArgumentException("Category You Requested Not Present " +
                               "For Your Brand Please Add It First And Try Again "));
       safelyDeleteCategoryIcon(category.getPublicId());
       this.categoryRepository.deleteById(categoryId);
    }

    @Transactional
    public CategoryResponse patchBrandCategory(UUID categoryId, CategoryPatchRequest patchRequest){
        // first we need to get the  brand owner
        String externalId= this.getCurrentBrand();
        // make sure this category is present for this brand

        Category category= this.categoryRepository.findByIdAndBrand_User_ExternalUserId(categoryId,externalId)
                .orElseThrow(()->new IllegalArgumentException("Category You Requested Not Present " +
                        "For Your Brand Please Add It First And Try Again "));

        Category pathchedCategory=patchCategoryProcess(category,patchRequest);

        if (patchRequest.parentCategoryId() != null) {
            Category parent = categoryRepository.findById(patchRequest.parentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            pathchedCategory.setParentCategory(parent);
        }
        if(patchRequest.imageIcon()!=null){
            safelyDeleteCategoryIcon(pathchedCategory.getPublicId());
            UploadResponse image = this.imageService.uploadImage(patchRequest.imageIcon());
            pathchedCategory.setImageUrl(image.imageUrl());
            pathchedCategory.setPublicId(image.publicId());
        }
       return toResponse(this.categoryRepository.saveAndFlush(pathchedCategory));
    }

    private Category patchCategoryProcess(Category category, CategoryPatchRequest patchRequest) {
        if(patchRequest.categoryNameEn()!=null&&!patchRequest.categoryNameEn().isBlank()){
            category.setCategoryNameEn(patchRequest.categoryNameEn());
        }
        if(patchRequest.categoryNameAr()!=null&&!patchRequest.categoryNameAr().isBlank()){
            category.setCategoryNameEn(patchRequest.categoryNameAr());
        }
        if(patchRequest.categoryDescriptionEn()!=null&&!patchRequest.categoryDescriptionEn().isBlank()){
            category.setCategoryNameEn(patchRequest.categoryNameEn());
        }
        if(patchRequest.categoryDescriptionAr()!=null&&!patchRequest.categoryDescriptionAr().isBlank()){
            category.setCategoryNameEn(patchRequest.categoryDescriptionAr());
        }
        return category;
    }

    private void safelyDeleteCategoryIcon(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        this.imageService.deleteImage(publicId);
    }

    private CategoryResponse toResponse(Category savedCategory) {
        UUID parentCategoryId= savedCategory.getParentCategory()==null? null:savedCategory.getParentCategory().getId();
        return  new CategoryResponse(
                savedCategory.getId(),
                savedCategory.getCategoryNameEn(),
                savedCategory.getCategoryDescriptionEn(),
                savedCategory.getCategoryNameAr(),
                savedCategory.getCategoryDescriptionAr(),
                savedCategory.getImageUrl(),
                savedCategory.getCategoryGender(),
                parentCategoryId
                );
    }

    private void validateCategoryCreateRequest(CategoryCreateRequest request) {
        if(request.categoryNameAr()==null){
            throw new IllegalArgumentException("Please enter category name in AR");
        }
        if(request.categoryNameEn()==null){
            throw new IllegalArgumentException("Please enter category name in EN");
        }
        if(request.categoryDescriptionEn()==null){
            throw new IllegalArgumentException("please enter category description in EN");
        }
        if(request.categoryDescriptionAr()==null){
            throw new IllegalArgumentException("please enter category description in AR");
        }
        if(request.categoryIcon()==null){
            throw new IllegalArgumentException("please enter category icon ");
        }
        if(request.categoryGender()==null){
            throw new IllegalArgumentException("please enter gender of category ");
        }
    }

    private Map<String, Object> mapPaginatedResponse(Page<findAllByBrandId> categoryPage) {
        return Map.of(
                "data", categoryPage.getContent(),
                "totalElements",categoryPage.getTotalElements(),
                "page",categoryPage.getNumber(),
                "size",categoryPage.getSize(),
                "totalPages",categoryPage.getTotalPages()
        );
    }

    private String getCurrentBrand() {
        // first we get the basic external Id
        String basicUserId=this.currentUserProvider.externalId();
        this.brandRepository.findByUser_ExternalUserId(basicUserId)
                .orElseThrow(()->new IllegalArgumentException("You need to setup your brand profile first"));
        return basicUserId;
    }

}
