package org.stylehub.backend.e_commerce.customer.controller;

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
public class CustomerBrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<BrandProfileRes> findBrandProfile(@PathVariable String brandId) {
        return ResponseEntity.ok(this.brandService.findBrandProfile(brandId));
    }
}
