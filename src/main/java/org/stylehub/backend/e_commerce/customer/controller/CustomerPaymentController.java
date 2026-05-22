package org.stylehub.backend.e_commerce.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.customer.dto.payment.PaymentResponse;
import org.stylehub.backend.e_commerce.order.payment.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Payments", description = "Customer payment result callbacks for order checkout.")
public class CustomerPaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Mark payment success", description = "Marks the order payment as successful.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment marked as successful"),
            @ApiResponse(responseCode = "400", description = "Invalid payment state transition"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot update this payment"),
            @ApiResponse(responseCode = "404", description = "Order or payment was not found")
    })
    @PostMapping("/{orderId}/success")
    public ResponseEntity<PaymentResponse> successPayment(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(paymentService.successPayment(orderId));
    }

    @Operation(summary = "Mark payment failure", description = "Marks the order payment as failed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment marked as failed"),
            @ApiResponse(responseCode = "400", description = "Invalid payment state transition"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot update this payment"),
            @ApiResponse(responseCode = "404", description = "Order or payment was not found")
    })
    @PostMapping("/{orderId}/failure")
    public ResponseEntity<PaymentResponse> failPayment(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(paymentService.failPayment(orderId));
    }
}
