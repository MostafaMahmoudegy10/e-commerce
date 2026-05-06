package org.stylehub.backend.e_commerce.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.customer.dto.AddToCartRequest;
import org.stylehub.backend.e_commerce.customer.service.CustomerCartService;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/customer/brands/{brandId}/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole({'CUSTOMER','BRAND_OWNER'})")
public class CustomerCartController {

    private final CustomerCartService cartService;

    @PostMapping()
    public ResponseEntity<Map<String,Integer>>addToCart(
            @PathVariable("brandId") String brandId
            ,@ModelAttribute AddToCartRequest request){
       return ResponseEntity.ok(cartService.upsertToCart(brandId,request));
    }
}
