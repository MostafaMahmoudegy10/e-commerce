package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BrandCalendarEventResponse(
        UUID orderId,
        String orderNumber,
        String customerName,
        String customerEmail,
        BrandCalendarEventType eventType,
        String title,
        Instant eventTime,
        OrderStatus orderStatus,
        BigDecimal totalPrice
) {
}
