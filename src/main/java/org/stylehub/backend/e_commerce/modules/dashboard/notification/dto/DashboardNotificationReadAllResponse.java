package org.stylehub.backend.e_commerce.modules.dashboard.notification.dto;

public record DashboardNotificationReadAllResponse(
        long markedAsReadCount,
        long totalNotifications,
        long unread,
        long read
) {
}
