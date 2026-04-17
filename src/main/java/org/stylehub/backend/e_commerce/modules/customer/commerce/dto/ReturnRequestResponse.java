package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import org.stylehub.backend.e_commerce.order.entity.ReturnRequestStatus;

import java.sql.Timestamp;
import java.util.UUID;

public record ReturnRequestResponse(
        UUID returnRequestId,
        UUID orderId,
        String customerEmail,
        ReturnRequestStatus status,
        String reason,
        String brandResponse,
        Timestamp createdAt,
        Timestamp resolvedAt
) {
}
