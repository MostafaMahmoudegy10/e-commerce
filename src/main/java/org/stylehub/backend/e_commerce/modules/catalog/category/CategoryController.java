package org.stylehub.backend.e_commerce.modules.catalog.category;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/categories")
@AllArgsConstructor
@PreAuthorize("hasAnyRole('CUSTOMER','BRAND_OWNER')")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<Map<String,Object>>getAllCategories(
            @RequestParam("brandId") UUID brandId,
            @PageableDefault(page = 0,size=10) Pageable pageable
    ) {
        return ResponseEntity.ok(this.categoryService.findAllBrandCategories(pageable,brandId));
    }
}
