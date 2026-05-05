package org.stylehub.backend.e_commerce.customer.dto;

import java.util.UUID;

public record AddToCartRequest(
        UUID productItemId,
        UUID productId,
        String brandExternalId,
        Integer quantity
) {
}
