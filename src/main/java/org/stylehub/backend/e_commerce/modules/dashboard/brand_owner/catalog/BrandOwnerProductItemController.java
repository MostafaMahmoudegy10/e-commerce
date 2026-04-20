package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.ProductItemCreateRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.ProductItemPatchRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.ProductItemResponse;
import org.stylehub.backend.e_commerce.product.product_item.service.ProductItemService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/product/{productId}/items")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
public class BrandOwnerProductItemController {

    private final ProductItemService   productItemService;

    @PostMapping
    public ResponseEntity<ProductItemResponse> createProductItem(
            @PathVariable UUID productId,
            @ModelAttribute ProductItemCreateRequest request
    ) {
        return ResponseEntity.ok(this.productItemService.createProductItem(productId, request));
    }

    @PatchMapping("{productItemId}")
    public ResponseEntity<ProductItemResponse> updateProductItem(
            @PathVariable UUID productId,
            @PathVariable UUID productItemId,
            @ModelAttribute ProductItemPatchRequest request
    ) {
        return ResponseEntity.ok(this.productItemService.updateProductItem(productId, productItemId, request));
    }

    @DeleteMapping("{productItemId}")
    public ResponseEntity<String> deleteProductItem(
            @PathVariable UUID productId,
            @PathVariable UUID productItemId
    ) {
        this.productItemService.deleteProductItem(productId, productItemId);
        return ResponseEntity.ok("Product item deleted");
    }
}
