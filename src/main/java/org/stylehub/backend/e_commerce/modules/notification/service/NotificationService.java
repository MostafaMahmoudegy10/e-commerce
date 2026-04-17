package org.stylehub.backend.e_commerce.modules.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.customer.commerce.dto.NotificationResponse;
import org.stylehub.backend.e_commerce.modules.notification.entity.Notification;
import org.stylehub.backend.e_commerce.modules.notification.repository.NotificationRepository;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void notifyUserByExternalId(String externalUserId, String title, String message, String type, String referenceId) {
        User user = this.userRepository.findByExternalUserId(externalUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for notification"));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notification.setReferenceId(referenceId);
        notification.setIsRead(false);
        this.notificationRepository.save(notification);
    }

    public List<NotificationResponse> findNotifications(String externalUserId) {
        return this.notificationRepository.findTop50ByUser_ExternalUserIdOrderByCreatedAtDesc(externalUserId)
                .stream().map(this::toResponse).toList();
    }

    public NotificationResponse markAsRead(UUID notificationId, String externalUserId) {
        Notification notification = this.notificationRepository.findByIdAndUser_ExternalUserId(notificationId, externalUserId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setIsRead(true);
        return toResponse(this.notificationRepository.save(notification));
    }

    public long unreadCount(String externalUserId) {
        return this.notificationRepository.countByUser_ExternalUserIdAndIsReadFalse(externalUserId);
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getNotificationType(),
                notification.getReferenceId(),
                notification.getIsRead(),
                notification.getCreatedAt()
        );
    }
}
