package org.stylehub.backend.e_commerce.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Customer Orders", description = "Checkout and customer order creation flow.")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @Operation(summary = "Checkout and create order", description = "Creates an order from the customer's cart for the selected brand.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid checkout request, empty cart, or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not a customer"),
            @ApiResponse(responseCode = "404", description = "Brand, cart, or shipping data was not found")
    })
    @PostMapping
    public ResponseEntity<CheckoutResponse>createOrder(@ModelAttribute OrderCreationRequest orderCreationRequest,
                                                     @PathVariable("brandId") String brandId){
        return ResponseEntity.ok(this.customerOrderService.createOrder(orderCreationRequest,brandId));
    }

}
