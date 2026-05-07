package org.stylehub.backend.e_commerce.customer.dto.cart;

import java.util.UUID;

public record AddToCartRequest(
        UUID productVariantId,
        Integer quantity
) {
}
