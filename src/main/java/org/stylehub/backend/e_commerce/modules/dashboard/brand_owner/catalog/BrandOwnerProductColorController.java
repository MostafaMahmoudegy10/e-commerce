package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.color.ProductColorCreationRequest;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;
import org.stylehub.backend.e_commerce.product.color.service.ProductColorService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/brands/product/{productId}/colors")
@RequiredArgsConstructor
public class BrandOwnerProductColorController {

    private final ProductColorService  productColorService;

    @PostMapping
    public ResponseEntity<Object> addNewProductColor(@PathVariable UUID productId,
                                                       @ModelAttribute ProductColorCreationRequest productColorCreationRequest) {
        return ResponseEntity.ok(this.productColorService.upsertNewProductColor(productColorCreationRequest, productId));
    }
}
