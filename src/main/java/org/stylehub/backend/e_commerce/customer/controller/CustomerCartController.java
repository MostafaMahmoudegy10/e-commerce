package org.stylehub.backend.e_commerce.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.customer.dto.cart.AddToCartRequest;
import org.stylehub.backend.e_commerce.customer.dto.cart.CartItemViewResponse;
import org.stylehub.backend.e_commerce.customer.service.CustomerCartService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/customer/brands/{brandId}/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole({'CUSTOMER'})")
public class CustomerCartController {

    private final CustomerCartService cartService;

    @PostMapping()
    public ResponseEntity<Map<String,Integer>>addToCart(
            @PathVariable("brandId") String brandId
            ,@ModelAttribute AddToCartRequest request){
       return ResponseEntity.ok(cartService.upsertToCart(brandId,request));
    }
    @GetMapping()
    public ResponseEntity<PageResponse<CartItemViewResponse>> viewCart(@PathVariable("brandId")String brandId
    ,@PageableDefault(size = 10) Pageable pageable){
        return ResponseEntity.ok(this.cartService.viewCart(brandId,pageable));
    }
    @DeleteMapping("/{cartId}/items/{cartItemId}")
    public ResponseEntity<String> removeFromCart(@PathVariable("cartId") UUID cartId, @PathVariable("cartItemId") UUID cartItemId){
        return ResponseEntity.ok(this.cartService.removeFromCart(cartId,cartItemId));
    }
}
