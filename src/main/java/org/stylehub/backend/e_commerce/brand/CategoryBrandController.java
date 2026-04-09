package org.stylehub.backend.e_commerce.brand;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.product.category.CategoryService;
import org.stylehub.backend.e_commerce.product.category.dto.CategoryCreateRequest;
import org.stylehub.backend.e_commerce.product.category.dto.CategoryResponse;
import org.stylehub.backend.e_commerce.product.category.dto.FindAllCategoryResponse;

import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/brands")
@AllArgsConstructor
public class CategoryBrandController {

    private CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse>addNewCategory(
//            @PathVariable UUID brandId,
            @ModelAttribute CategoryCreateRequest request){
       CategoryResponse response=this.categoryService.addNewCategory(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String,Object>> getAllCategories
            (@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                this.categoryService.findAllCategories(pageable)
        );
    }
}
