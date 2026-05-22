package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.color.BrandProductColorViewResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.color.ProductColorCreationRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.color.ProductColorDeleteResponse;
import org.stylehub.backend.e_commerce.product.color.service.ProductColorService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/product/{productId}/colors")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BRAND_OWNER')")
@Tag(name = "Brand Product Colors", description = "Manage color options and color images for brand products.")
public class BrandOwnerProductColorController {

    private final ProductColorService  productColorService;

    @Operation(summary = "Add product color", description = "Adds or updates a color option for a brand-owned product.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product color saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid color data or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not allowed to manage this product"),
            @ApiResponse(responseCode = "404", description = "Product was not found")
    })
    @PostMapping
    public ResponseEntity<Object> addNewProductColor(@PathVariable UUID productId,
                                                       @ModelAttribute ProductColorCreationRequest productColorCreationRequest) {
        return ResponseEntity.ok(this.productColorService.upsertNewProductColor(productColorCreationRequest, productId));
    }

    @GetMapping
    public ResponseEntity<List<BrandProductColorViewResponse>> findProductColors(@PathVariable UUID productId) {
        return ResponseEntity.ok(this.productColorService.findBrandProductColors(productId));
    }

    @DeleteMapping("/{colorId}")
    public ResponseEntity<ProductColorDeleteResponse> deleteProductColor(
            @PathVariable UUID productId,
            @PathVariable UUID colorId
    ) {
        return ResponseEntity.ok(productColorService.deleteProductColor(productId, colorId));
    }
}
