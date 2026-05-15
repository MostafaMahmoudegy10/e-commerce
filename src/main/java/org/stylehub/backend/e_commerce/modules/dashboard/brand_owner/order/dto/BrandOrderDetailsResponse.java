package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentMethod;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BrandOrderDetailsResponse(
        UUID orderId,
        String orderNumber,
        UUID brandId,
        String brandName,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        PaymentMethod paymentMethod,
        BigDecimal totalPrice,
        Timestamp createdAt,
        UUID customerId,
        String customerName,
        String customerEmail,
        String customerPhoneNumber,
        String customerProfileImageUrl,
        BrandOrderShippingAddressResponse shippingAddress,
        List<BrandOrderItemDetailsResponse> items,
        Instant paidAt,
        Instant shippedAt,
        Instant deliveredAt,
        Instant cancelledAt
) {
}
