package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import org.stylehub.backend.e_commerce.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public record CustomerOrderResponse(
        UUID orderId,
        UUID brandId,
        String brandName,
        OrderStatus orderStatus,
        BigDecimal totalPrice,
        Timestamp createdAt,
        Timestamp paidAt,
        Timestamp shippedAt,
        Timestamp deliveredAt,
        Timestamp cancelledAt,
        Timestamp estimatedDeliveryAt,
        CustomerAddressResponse shippingAddress,
        List<OrderTimelineEventResponse> timeline,
        List<OrderItemResponse> items,
        PaymentResponse payment
){
}
