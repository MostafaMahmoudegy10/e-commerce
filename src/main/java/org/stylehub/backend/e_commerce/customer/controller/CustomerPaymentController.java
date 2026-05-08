package org.stylehub.backend.e_commerce.customer.controller;

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
public class CustomerPaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}/success")
    public ResponseEntity<PaymentResponse> successPayment(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(paymentService.successPayment(orderId));
    }

    @PostMapping("/{orderId}/failure")
    public ResponseEntity<PaymentResponse> failPayment(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(paymentService.failPayment(orderId));
    }
}