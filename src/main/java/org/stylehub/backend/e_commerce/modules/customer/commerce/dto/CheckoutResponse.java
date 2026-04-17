package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CheckoutResponse(
        UUID orderId,
        UUID brandId,
        String brandName,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        List<OrderItemResponse> items,
        PaymentResponse payment
) {
}
