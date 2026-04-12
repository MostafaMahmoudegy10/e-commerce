package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationRequest;
import org.stylehub.backend.e_commerce.product.dto.ProductCreationResponse;
import org.stylehub.backend.e_commerce.product.service.ProductService;

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
}
