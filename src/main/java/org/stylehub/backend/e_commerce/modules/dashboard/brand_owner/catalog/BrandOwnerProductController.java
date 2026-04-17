package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationRequest;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationResponse;
import org.stylehub.backend.e_commerce.product.dto.ProductPatchRequest;
import org.stylehub.backend.e_commerce.product.service.ProductService;
import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemCreateRequest;
import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemPatchRequest;
import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemResponse;
import org.stylehub.backend.e_commerce.product.product_item.service.ProductItemService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/product")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
public class BrandOwnerProductController {

    private final ProductService productService;
    private final ProductItemService productItemService;

    @PostMapping
    public ResponseEntity<ProductCreationResponse>addNewProduct(@ModelAttribute ProductCreationRequest request) {
        return ResponseEntity.ok(this.productService.addNewProduct(request));
    }

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

    @PostMapping("{productId}/items")
    public ResponseEntity<ProductItemResponse> addNewProductItem(
            @PathVariable UUID productId,
            @ModelAttribute ProductItemCreateRequest request
    ) {
        return ResponseEntity.ok(this.productItemService.addNewProductItem(productId, request));
    }

    @PatchMapping("items/{productItemId}")
    public ResponseEntity<ProductItemResponse> patchProductItem(
            @PathVariable UUID productItemId,
            @ModelAttribute ProductItemPatchRequest request
    ) {
        return ResponseEntity.ok(this.productItemService.patchProductItem(productItemId, request));
    }

    @DeleteMapping("items/{productItemId}")
    public ResponseEntity<String> deleteProductItem(@PathVariable UUID productItemId) {
        this.productItemService.deleteProductItem(productItemId);
        return ResponseEntity.ok("Product item deleted");
    }
}
