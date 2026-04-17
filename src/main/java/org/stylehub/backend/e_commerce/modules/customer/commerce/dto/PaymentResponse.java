package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import org.stylehub.backend.e_commerce.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        BigDecimal amount,
        String paymentMethod,
        String transactionId,
        PaymentStatus paymentStatus
) {
}
