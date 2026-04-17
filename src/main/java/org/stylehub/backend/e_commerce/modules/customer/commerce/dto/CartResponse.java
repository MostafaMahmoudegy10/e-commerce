package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID cartId,
        List<CartItemResponse> items,
        BigDecimal totalPrice
) {
}
