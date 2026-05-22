package org.stylehub.backend.e_commerce.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Ratings", description = "Customer product ratings and model collaboration reviews.")
public class CustomerRatingController {

    private final ProductRatingService productRatingService;

    @Operation(summary = "Create or update product rating", description = "Creates or updates the authenticated customer's rating for a product.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product rating saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rating data or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not a customer"),
            @ApiResponse(responseCode = "404", description = "Brand or product was not found")
    })
    @PostMapping
    public ResponseEntity<ProductRatingCreationResponse> upsertNewRate(
            @PathVariable("brandId")String brandId,@PathVariable("productId") UUID productId, @ModelAttribute ProductRatingCreation productRatingCreation) {
        return ResponseEntity.ok(this.productRatingService.
                upsertNewRate(brandId,productId,productRatingCreation));
    }
}
