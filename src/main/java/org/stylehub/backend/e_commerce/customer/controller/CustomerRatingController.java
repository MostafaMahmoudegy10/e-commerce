package org.stylehub.backend.e_commerce.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.customer.dto.rating.ProductRatingCreation;
import org.stylehub.backend.e_commerce.customer.dto.rating.ProductRatingCreationResponse;
import org.stylehub.backend.e_commerce.customer.rating.product_rating.entity.ProductRating;
import org.stylehub.backend.e_commerce.customer.rating.product_rating.service.ProductRatingService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer/brands/{brandId}/products/{productId}/rating")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerRatingController {

    private final ProductRatingService productRatingService;

    @PostMapping
    public ResponseEntity<ProductRatingCreationResponse> upsertNewRate(
            @PathVariable("brandId")String brandId,@PathVariable("productId") UUID productId, @ModelAttribute ProductRatingCreation productRatingCreation) {
        return ResponseEntity.ok(this.productRatingService.
                upsertNewRate(brandId,productId,productRatingCreation));
    }
}
