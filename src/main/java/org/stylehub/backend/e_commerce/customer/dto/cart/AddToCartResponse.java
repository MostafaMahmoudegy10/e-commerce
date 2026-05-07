package org.stylehub.backend.e_commerce.customer.dto.cart;

import java.util.UUID;

public record AddToCartResponse(
        UUID productId
) {
}
