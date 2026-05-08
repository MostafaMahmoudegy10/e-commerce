package org.stylehub.backend.e_commerce.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.customer.dto.order.CheckoutResponse;
import org.stylehub.backend.e_commerce.customer.dto.order.OrderCreationRequest;
import org.stylehub.backend.e_commerce.customer.service.CustomerOrderService;

@RestController
@RequestMapping("api/v1/customer/brands/{brandId}/checkout")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @PostMapping
    public ResponseEntity<CheckoutResponse>createOrder(@ModelAttribute OrderCreationRequest orderCreationRequest,
                                                     @PathVariable("brandId") String brandId){
        return ResponseEntity.ok(this.customerOrderService.createOrder(orderCreationRequest,brandId));
    }

}
