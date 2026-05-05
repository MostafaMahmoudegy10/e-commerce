package org.stylehub.backend.e_commerce.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.customer.dto.product.ProductDetailsDto;
import org.stylehub.backend.e_commerce.customer.service.CustomerProductService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer/brands/{brandId}/products/{productId}")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole({'CUSTOMER','BRAND_OWNER'})")
public class CustomerProductController {

    private final CustomerProductService customerProductService;
    @GetMapping
    public ResponseEntity<ProductDetailsDto> findProductDetails(@PathVariable("brandId")String brandId, @PathVariable("productId") UUID productId){
        return ResponseEntity.ok(customerProductService.findProductDetails(brandId,productId));
    }
}
