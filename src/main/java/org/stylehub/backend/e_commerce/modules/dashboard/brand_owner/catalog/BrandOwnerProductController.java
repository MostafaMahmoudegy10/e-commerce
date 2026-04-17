package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationRequest;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationResponse;
import org.stylehub.backend.e_commerce.product.dto.ProductPatchRequest;
import org.stylehub.backend.e_commerce.product.service.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/product")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
public class BrandOwnerProductController {

    private final ProductService productService;

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
}
