package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.BrandProductVariantViewResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantCreationRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantCreationResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantStockUpdateRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantStockUpdateResponse;
import org.stylehub.backend.e_commerce.product.color.variant.service.ProductVariantService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/product/{productId}/colors/{colorId}/variants")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
@Tag(name = "Brand Product Variants", description = "Manage size, price, and inventory variants for product colors.")
public class BrandOwnerProductVariantController {

    private final ProductVariantService  productVariantService;

    @Operation(summary = "Add product variant", description = "Adds or updates a variant under a specific product color.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product variant saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid variant data or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not allowed to manage this product"),
            @ApiResponse(responseCode = "404", description = "Product or color was not found")
    })
    @PostMapping
    public ResponseEntity<ProductVariantCreationResponse>upsertVariant(
            @ModelAttribute ProductVariantCreationRequest productVariantCreationRequest,
            @PathVariable("productId") UUID productId,
            @PathVariable("colorId") UUID colorId
    ){
        return ResponseEntity.ok(this.productVariantService.upsertVariant(productVariantCreationRequest,productId,colorId));
    }

    @GetMapping
    public ResponseEntity<List<BrandProductVariantViewResponse>> findProductVariants(
            @PathVariable UUID productId,
            @PathVariable UUID colorId
    ) {
        return ResponseEntity.ok(this.productVariantService.findBrandProductVariants(productId, colorId));
    }

    @Operation(summary = "Patch variant stock", description = "Updates available stock for a specific product variant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Variant stock updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid stock value or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not allowed to manage this variant"),
            @ApiResponse(responseCode = "404", description = "Product, color, or variant was not found")
    })
    @PatchMapping("/{variantId}/stock")
    public ResponseEntity<ProductVariantStockUpdateResponse> patchVariantStock(
            @PathVariable UUID productId,
            @PathVariable UUID colorId,
            @PathVariable UUID variantId,
            @RequestBody ProductVariantStockUpdateRequest request
    ) {
        return ResponseEntity.ok(productVariantService.patchVariantStock(productId, colorId, variantId, request));
    }
}
