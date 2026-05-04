package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantCreationRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantCreationResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantStockUpdateRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.variant.ProductVariantStockUpdateResponse;
import org.stylehub.backend.e_commerce.product.color.variant.service.ProductVariantService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/product/{productId}/colors/{colorId}/variants")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
public class BrandOwnerProductVariantController {

    private final ProductVariantService  productVariantService;

    @PostMapping
    public ResponseEntity<ProductVariantCreationResponse>upsertVariant(
            @ModelAttribute ProductVariantCreationRequest productVariantCreationRequest,
            @PathVariable("productId") UUID productId,
            @PathVariable("colorId") UUID colorId
    ){
        return ResponseEntity.ok(this.productVariantService.upsertVariant(productVariantCreationRequest,productId,colorId));
    }

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
