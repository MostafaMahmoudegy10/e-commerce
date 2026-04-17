package org.stylehub.backend.e_commerce.modules.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.modules.notification.entity.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findTop50ByUser_ExternalUserIdOrderByCreatedAtDesc(String externalUserId);

    Optional<Notification> findByIdAndUser_ExternalUserId(UUID notificationId, String externalUserId);

    long countByUser_ExternalUserIdAndIsReadFalse(String externalUserId);
}
