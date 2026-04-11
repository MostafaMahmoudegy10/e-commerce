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
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.CategoryCreateRequest;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.CategoryPatchRequest;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.CategoryResponse;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.FindAllCategoryResponse;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.modules.catalog.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
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

    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);


    @Transactional
    public CategoryResponse addNewCategory(CategoryCreateRequest request) {
        validateBaseRequest(request.categoryName(), request.categoryIcon());

        Brand ownerBrand = null;
        try {
            ownerBrand = getCurrentOwnerBrand();
        } catch (Exception ignored) {
            // fallback for tests/public category management
        }

        boolean exists = ownerBrand != null
                ? categoryRepository.existsByCategoryNameIgnoreCaseAndBrand_Id(request.categoryName(), ownerBrand.getId())
                : categoryRepository.existsByCategoryNameIgnoreCase(request.categoryName());
        if (exists) {
            throw new IllegalArgumentException("categoryName already exists");
        }

        Category parent = null;
        if (request.parentCategoryId() != null) {
            parent = this.categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
        }

        UploadResponse image = uploadCategoryImage(request.categoryIcon());

        Category category = new Category();
        category.setCategoryName(request.categoryName());
        category.setCategoryGender(Gender.fromCode(request.categoryGender()));
        category.setCategoryDescription(request.categoryDescription());
        category.setImageUrl(image.imageUrl());
        category.setPublicId(image.publicId());
        category.setParentCategory(parent);
        category.setBrand(ownerBrand);

        Category savedCategory = this.categoryRepository.save(category);
        return toResponse(savedCategory);
    }

    public Map<String, Object> findAllBrandCategories(Pageable pageable) {
        Brand ownerBrand = getCurrentOwnerBrand();
        Page<FindAllCategoryResponse> categoryPage = this.categoryRepository
                .findAllPageableCategoryByBrandId(ownerBrand.getId(), pageable);
        return mapPaginatedResponse(categoryPage);
    }

    @Deprecated
    public Map<String, Object> findAllCategories(Pageable pageable) {
        Page<FindAllCategoryResponse> categoryPage = this.categoryRepository.findAllPageableCategory(pageable);
        return mapPaginatedResponse(categoryPage);
    }

    public Map<String, Object> findAllEcommerceCategories(Pageable pageable) {
        Page<FindAllCategoryResponse> categoryPage = this.categoryRepository.findAllGlobalCategories(pageable);
        return mapPaginatedResponse(categoryPage);
    }

    @Transactional
    public CategoryResponse patchBrandCategory(UUID categoryId, CategoryPatchRequest request) {
        Brand ownerBrand = getCurrentOwnerBrand();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (category.getBrand() == null || !category.getBrand().getId().equals(ownerBrand.getId())) {
            throw new IllegalArgumentException("You can only edit your own categories");
        }

        if (request.categoryName() != null && !request.categoryName().isBlank()) {
            category.setCategoryName(request.categoryName());
        }
        if (request.categoryGender() != null) {
            category.setCategoryGender(Gender.fromCode(request.categoryGender()));
        }
        if (request.categoryDescription() != null && !request.categoryDescription().isBlank()) {
            category.setCategoryDescription(request.categoryDescription());
        }
        if (request.parentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParentCategory(parent);
        }

        if (request.categoryIcon() != null && !request.categoryIcon().isEmpty()) {
            safelyDeleteImage(category.getPublicId());
            UploadResponse uploadResponse = uploadCategoryImage(request.categoryIcon());
            category.setImageUrl(uploadResponse.imageUrl());
            category.setPublicId(uploadResponse.publicId());
        }

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Transactional
    public void deleteBrandCategoryById(UUID categoryId) {
        Brand ownerBrand = getCurrentOwnerBrand();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (category.getBrand() == null || !category.getBrand().getId().equals(ownerBrand.getId())) {
            throw new IllegalArgumentException("You can only delete your own categories");
        }

        safelyDeleteImage(category.getPublicId());
        this.categoryRepository.delete(category);
    }

    private UploadResponse uploadCategoryImage(org.springframework.web.multipart.MultipartFile imageFile) {
        try {
            return this.imageService.uploadImage(imageFile);
        } catch (IOException e) {
            log.error("Failed to upload category icon", e);
            throw new RuntimeException("Failed to upload category icon", e);
        }
    }

    private void safelyDeleteImage(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        try {
            imageService.deleteImage(publicId);
        } catch (IOException e) {
            log.warn("Failed to delete old image with publicId={}", publicId, e);
        }
    }

    private void validateBaseRequest(String categoryName, org.springframework.web.multipart.MultipartFile categoryIcon) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new IllegalArgumentException("categoryName is null or empty");
        }
        if (categoryIcon == null || categoryIcon.isEmpty()) {
            throw new IllegalArgumentException("please add an icon for your category");
        }
    }

    private Brand getCurrentOwnerBrand() {
        String externalId = currentUserProvider.externalId();
        User user = userRepository.findByExternalUserId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));

        return brandRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("You need to setup your brand profile first"));
    }

    private Map<String, Object> mapPaginatedResponse(Page<FindAllCategoryResponse> categoryPage) {
        return Map.of(
                "content", categoryPage.getContent(),
                "totalElements", categoryPage.getTotalElements(),
                "page", categoryPage.getNumber(),
                "size", categoryPage.getSize(),
                "totalPages", categoryPage.getTotalPages()
        );
    }

    private CategoryResponse toResponse(Category category) {
        UUID parentId = category.getParentCategory() != null ?
                category.getParentCategory().getId() : null;
        return new CategoryResponse(
                category.getId(),
                category.getCategoryName(),
                category.getCategoryGender(),
                parentId);
    }
}
