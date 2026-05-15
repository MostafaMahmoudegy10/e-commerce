package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.modules.catalog.category.CategoryService;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.CategoryCreateRequest;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.CategoryPatchRequest;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.CategoryResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.category.BrandCategoryFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.category.BrandCategoryStatsResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.category.BrandCategoryViewResponse;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;

import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/brands/categories")
@AllArgsConstructor
@PreAuthorize("hasRole('BRAND_OWNER')")
public class BrandOwnerCategoryController {

    private final CategoryService categoryService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping()
    public ResponseEntity<CategoryResponse> addNewCategory(
            @ModelAttribute CategoryCreateRequest request) {

        CategoryResponse response = this.categoryService.addNewCategory(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<BrandCategoryViewResponse>> findBrandCategories(
            @ModelAttribute BrandCategoryFilterRequest filter,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(this.categoryService.findBrandCategories(filter, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<BrandCategoryStatsResponse> findBrandCategoryStats() {
        return ResponseEntity.ok(this.categoryService.getBrandCategoryStats());
    }

    @DeleteMapping("{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID categoryId) {
        this.categoryService.deleteCategoryOfBrand(categoryId);
        return ResponseEntity.ok("Category deleted");
    }

    @PatchMapping("{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable("categoryId") UUID CategoryId, @ModelAttribute CategoryPatchRequest patchRequest) {
        return ResponseEntity.ok(this.categoryService.patchBrandCategory(CategoryId, patchRequest));
    }

}
