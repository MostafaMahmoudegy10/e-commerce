package org.stylehub.backend.e_commerce.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.customer.dto.product.FindAllProductsResponse;
import org.stylehub.backend.e_commerce.customer.dto.product.ProductDetailsDto;
import org.stylehub.backend.e_commerce.customer.dto.product.FindAllProductFilterRequest;
import org.stylehub.backend.e_commerce.customer.dto.product.ProductSummary;
import org.stylehub.backend.e_commerce.customer.service.CustomerProductService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer/brands/{brandId}/products")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole({'CUSTOMER','BRAND_OWNER'})")
@Tag(name = "Customer Products", description = "Browse brand products, view product details, and search catalog inventory.")
public class CustomerProductController {

    private final CustomerProductService customerProductService;

    @GetMapping()
    public ResponseEntity<PageResponse<FindAllProductsResponse>>findAllBrandProducts(
            @PathVariable("brandId") String brandId,
            @ModelAttribute FindAllProductFilterRequest filter,
            @PageableDefault(size = 10)Pageable pageable
            ){
        return ResponseEntity.ok(this.customerProductService.findAllBrandProducts(filter,pageable,brandId));
    }

    @Operation(summary = "Show product details", description = "Returns the full product page data including colors and variants.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product details returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product or brand request"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot access customer catalog"),
            @ApiResponse(responseCode = "404", description = "Product or brand was not found")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailsDto> findProductDetails(@PathVariable("brandId")String brandId, @PathVariable("productId") UUID productId){
        return ResponseEntity.ok(customerProductService.findProductDetails(brandId,productId));
    }

    @Operation(summary = "Product full-text search", description = "Searches products within a brand catalog using the full-text search index.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search request"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot access customer catalog"),
            @ApiResponse(responseCode = "404", description = "Brand was not found")
    })
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductSummary>>findProductSummary(
            @PathVariable("brandId") String brandId
            ,@RequestParam("search") String search, @PageableDefault(size = 10)Pageable pageable){
        return ResponseEntity.ok(this.customerProductService.findProductSummary(search,pageable,brandId));
    }


}
