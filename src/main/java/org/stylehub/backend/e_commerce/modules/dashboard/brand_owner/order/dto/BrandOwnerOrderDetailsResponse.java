package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import org.stylehub.backend.e_commerce.modules.customer.commerce.dto.OrderTimelineEventResponse;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record BrandOwnerOrderDetailsResponse(
        UUID orderId,
        UUID customerId,
        String customerEmail,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        Timestamp createdAt,
        Timestamp paidAt,
        Timestamp shippedAt,
        Timestamp deliveredAt,
        Timestamp cancelledAt,
        Timestamp estimatedDeliveryAt,
        String shippingRecipientName,
        String shippingPhoneNumber,
        String shippingAddressLine1,
        String shippingAddressLine2,
        String shippingCity,
        String shippingCountry,
        String shippingPostalCode,
        String paymentMethod,
        String transactionId,
        PaymentStatus paymentStatus,
        List<OrderTimelineEventResponse> timeline,
        List<BrandOwnerOrderItemResponse> items
) {
}
