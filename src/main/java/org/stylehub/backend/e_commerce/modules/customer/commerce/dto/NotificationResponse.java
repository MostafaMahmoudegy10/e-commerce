package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record NotificationResponse(
        UUID notificationId,
        String title,
        String message,
        String notificationType,
        String referenceId,
        Boolean isRead,
        Timestamp createdAt
) {
}
