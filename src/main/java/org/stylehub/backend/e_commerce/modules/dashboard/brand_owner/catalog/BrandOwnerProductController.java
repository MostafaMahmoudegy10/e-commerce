package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.ProductPatchRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product.BrandProductFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product.BrandProductViewResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.product.FindAllProductForBrand;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationRequest;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationResponse;
import org.stylehub.backend.e_commerce.product.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/product")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
@Tag(name = "Brand Product Management", description = "Create, update, delete, and browse products from the brand dashboard.")
public class BrandOwnerProductController {

    private final ProductService productService;

    @Operation(summary = "Create product", description = "Creates a new product for the authenticated brand owner.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not a brand owner")
    })
    @PostMapping
    public ResponseEntity<ProductCreationResponse>addNewProduct(@ModelAttribute ProductCreationRequest request) {
        return ResponseEntity.ok(this.productService.addNewProduct(request));
    }

    @Operation(summary = "Patch brand product", description = "Updates editable product fields for a brand-owned product.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not allowed to update this product"),
            @ApiResponse(responseCode = "404", description = "Product was not found")
    })
    @PatchMapping("{productId}")
    public ResponseEntity<ProductCreationResponse> patchProduct(
            @PathVariable UUID productId,
            @ModelAttribute ProductPatchRequest request
    ) {
        return ResponseEntity.ok(this.productService.patchBrandProduct(productId, request));
    }

    @DeleteMapping("{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID productId) {
        this.productService.deleteBrandProduct(productId);
        return ResponseEntity.ok("Product deleted");
    }
    @GetMapping
    public ResponseEntity<PageResponse<BrandProductViewResponse>> findAllProductsForBrand(
            @ModelAttribute BrandProductFilterRequest filter,
            @PageableDefault(size = 10) Pageable pageable
    ){
        return ResponseEntity.ok(this.productService.findAllProductsForBrand(filter, pageable));
    }

}
