package org.stylehub.backend.e_commerce.modules.dashboard.notification.dto;

import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotificationType;

import java.time.Instant;
import java.util.UUID;

public record DashboardNotificationViewResponse(
        UUID id,
        DashboardNotificationType type,
        String title,
        String message,
        String referenceType,
        UUID referenceId,
        String referenceCode,
        String actionUrl,
        Boolean isRead,
        Instant createdAt,
        Instant readAt
) {
}
