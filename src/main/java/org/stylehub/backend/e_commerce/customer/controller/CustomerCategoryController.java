package org.stylehub.backend.e_commerce.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.customer.profile.dto.category.CategoryNameDto;
import org.stylehub.backend.e_commerce.customer.service.CustomerCategoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer/brands/{brandId}/categories")
@RequiredArgsConstructor
@Tag(name = "Customer Categories", description = "Browse brand categories from the customer storefront.")
public class CustomerCategoryController {

    private final CustomerCategoryService customerCategoryService;

    @Operation(summary = "List customer categories", description = "Returns parent or child categories for a brand storefront.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category filter"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Brand or parent category was not found")
    })
    @GetMapping()
    public ResponseEntity<List<CategoryNameDto>>findAllParentCategories(
            @PathVariable UUID brandId,
            @RequestParam(required=false,name = "parentCategory")String parentCategory,
            @RequestParam(required = false, name = "parentCategoryId") UUID parentCategoryId
    ) {
        return ResponseEntity.ok(this.customerCategoryService.findAllParentCategories(brandId, parentCategory, parentCategoryId));
    }

}
