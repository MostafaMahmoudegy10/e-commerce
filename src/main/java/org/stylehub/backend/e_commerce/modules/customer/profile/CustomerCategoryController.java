package org.stylehub.backend.e_commerce.modules.customer.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.category.CategoryNameDto;
import org.stylehub.backend.e_commerce.modules.customer.profile.service.CustomerCategoryService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer/brand/{brandId}/categories")
@RequiredArgsConstructor
public class CustomerCategoryController {

    private final CustomerCategoryService customerCategoryService;

    @GetMapping()
    public ResponseEntity<List<CategoryNameDto>>findAllParentCategories(@PathVariable UUID brandId
        , @RequestParam(required=false,name = "parentCategory")String parentCategory
    ) {
        return ResponseEntity.ok(this.customerCategoryService.findAllParentCategories(brandId,parentCategory));
    }

}
