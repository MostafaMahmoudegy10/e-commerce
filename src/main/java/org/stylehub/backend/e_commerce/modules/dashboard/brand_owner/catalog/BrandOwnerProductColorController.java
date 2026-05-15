package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

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
public class BrandOwnerProductColorController {

    private final ProductColorService  productColorService;

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
