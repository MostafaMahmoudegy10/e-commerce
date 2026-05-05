package org.stylehub.backend.e_commerce.modules.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.customer.dto.AddToCartRequest;
import org.stylehub.backend.e_commerce.customer.dto.AddToCartResponse;
import org.stylehub.backend.e_commerce.customer.service.CustomerCartService;


import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer/brands/{brandId}/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerCartController {

    private final CustomerCartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Integer>addToCart(
            @PathVariable("brandId") String brandId
            ,@ModelAttribute AddToCartRequest request){
       return ResponseEntity.ok(cartService.upsertToCart(brandId,request));
    }
}
