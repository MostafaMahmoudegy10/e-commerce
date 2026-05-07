package org.stylehub.backend.e_commerce.customer.dto.cart;

import org.stylehub.backend.e_commerce.cart.entity.CartStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemViewResponse(
    String customerUserName,
    String brandName,
    String productNameEn,
    String productNameAr,
    String thumbnail,
    BigDecimal totalPrice,
    Integer quantity,
    UUID cartItemId,
    UUID cartId,
    CartStatus cartStatus,
    UUID productVariantId,
    String sku,
    String size,
    String colorCode,
    UUID productId
) {
}
