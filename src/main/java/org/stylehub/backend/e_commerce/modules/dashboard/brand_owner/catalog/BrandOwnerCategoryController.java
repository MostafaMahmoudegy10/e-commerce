package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.catalog.category.CategoryService;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.CategoryCreateRequest;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.CategoryPatchRequest;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.CategoryResponse;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/brands/categories")
@AllArgsConstructor
@PreAuthorize("hasRole('BRAND_OWNER')")
public class BrandOwnerCategoryController {

    private final CategoryService categoryService;

    @PostMapping()
    public ResponseEntity<CategoryResponse> addNewCategory(
            @ModelAttribute CategoryCreateRequest request) {

        CategoryResponse response = this.categoryService.addNewCategory(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllCategories
            (@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(this.categoryService.findAllBrandCategories(pageable));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteBrandCategory(@PathVariable UUID categoryId) {
        this.categoryService.deleteBrandCategoryById(categoryId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> patchCategory(@PathVariable UUID categoryId,
                                                          @ModelAttribute CategoryPatchRequest request) {
        return ResponseEntity.ok(categoryService.patchBrandCategory(categoryId, request));
    }
}
