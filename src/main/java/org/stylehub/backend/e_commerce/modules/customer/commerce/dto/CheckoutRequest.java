package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import java.util.UUID;

public record CheckoutRequest(
        UUID brandId,
        String paymentMethod,
        String transactionId
) {
}
