//package org.stylehub.backend.e_commerce.modules.customer.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.stylehub.backend.e_commerce.modules.customer.dto.AddToCartRequest;
//import org.stylehub.backend.e_commerce.modules.customer.dto.AddToCartResponse;
//import org.stylehub.backend.e_commerce.modules.customer.service.CustomerCartService;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("api/v1/customer/cart")
//@RequiredArgsConstructor
//@PreAuthorize("hasRole('CUSTOMER')")
//public class CustomerCartController {
//
//    private final CustomerCartService cartService;
//
//    @PostMapping("/add")
//    public ResponseEntity<AddToCartResponse>addToCart(@ModelAttribute AddToCartRequest request){
//       return ResponseEntity.ok(cartService.addToCart(request));
//    }
//}
