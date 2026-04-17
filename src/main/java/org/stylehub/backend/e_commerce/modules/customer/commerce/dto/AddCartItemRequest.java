package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import java.util.UUID;

public record AddCartItemRequest(
        UUID productItemId,
        String sizeName,
        Integer quantity
) {
}
