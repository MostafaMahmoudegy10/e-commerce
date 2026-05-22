package org.stylehub.backend.e_commerce.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.brand.dto.BrandProfileRes;
import org.stylehub.backend.e_commerce.brand.service.BrandService;

@RestController
@RequestMapping("api/v1/customer/brands/{brandId}")
@RequiredArgsConstructor
@Tag(name = "Customer Brands", description = "Customer-facing brand profile browsing.")
public class CustomerBrandController {

    private final BrandService brandService;

    @Operation(summary = "Show brand profile", description = "Returns the public storefront profile for a brand.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Brand profile returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid brand request"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Brand was not found")
    })
    @GetMapping
    public ResponseEntity<BrandProfileRes> findBrandProfile(@PathVariable String brandId) {
        return ResponseEntity.ok(this.brandService.findBrandProfile(brandId));
    }
}
