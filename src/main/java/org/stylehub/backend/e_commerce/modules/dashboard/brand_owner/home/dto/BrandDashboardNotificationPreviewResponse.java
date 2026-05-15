package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.home.dto;

import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotificationType;

import java.time.Instant;
import java.util.UUID;

public record BrandDashboardNotificationPreviewResponse(
        UUID notificationId,
        DashboardNotificationType type,
        String title,
        String message,
        String referenceCode,
        Instant createdAt
) {
}
