package org.stylehub.backend.e_commerce.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.customer.dto.product.FindAllProductsResponse;
import org.stylehub.backend.e_commerce.customer.dto.product.ProductDetailsDto;
import org.stylehub.backend.e_commerce.customer.dto.product.FindAllProductFilterRequest;
import org.stylehub.backend.e_commerce.customer.service.CustomerProductService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer/brands/{brandId}/products")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole({'CUSTOMER','BRAND_OWNER'})")
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
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailsDto> findProductDetails(@PathVariable("brandId")String brandId, @PathVariable("productId") UUID productId){
        return ResponseEntity.ok(customerProductService.findProductDetails(brandId,productId));
    }


}
