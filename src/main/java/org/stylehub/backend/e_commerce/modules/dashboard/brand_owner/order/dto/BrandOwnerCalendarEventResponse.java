package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BrandOwnerCalendarEventResponse(
        UUID orderId,
        LocalDate eventDate,
        String eventType,
        String title,
        OrderStatus currentOrderStatus,
        BigDecimal totalPrice,
        String customerEmail
) {
}
