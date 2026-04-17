package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

public record PaymentRetryRequest(
        String paymentMethod,
        String transactionId
) {
}
