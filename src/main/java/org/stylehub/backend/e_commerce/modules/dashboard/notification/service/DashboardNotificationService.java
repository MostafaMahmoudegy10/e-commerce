package org.stylehub.backend.e_commerce.modules.dashboard.notification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestAcceptedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestCreatedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestRejectedEvent;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationStatsResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationTypeCountResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationTypeCountRow;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationViewResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotification;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotificationType;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.repository.DashboardNotificationRepository;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardNotificationService {

    private final DashboardNotificationRepository dashboardNotificationRepository;
    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;

    public PageResponse<DashboardNotificationViewResponse> findNotifications(
            DashboardNotificationFilterRequest filter,
            Pageable pageable
    ) {
        Page<DashboardNotification> page = this.dashboardNotificationRepository.findNotifications(
                currentUserProvider.getUserId(),
                filter,
                pageable
        );

        List<DashboardNotificationViewResponse> items = page.getContent().stream()
                .map(this::mapToViewResponse)
                .toList();

        return new PageResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public DashboardNotificationStatsResponse getStats() {
        UUID userId = currentUserProvider.getUserId();

        return new DashboardNotificationStatsResponse(
                this.dashboardNotificationRepository.countByRecipientUser_Id(userId),
                this.dashboardNotificationRepository.countByRecipientUser_IdAndReadAtIsNull(userId),
                this.dashboardNotificationRepository.countByRecipientUser_IdAndReadAtIsNotNull(userId),
                this.dashboardNotificationRepository.countByType(userId).stream()
                        .map(this::mapTypeCount)
                        .toList()
        );
    }

    @Transactional
    public DashboardNotificationViewResponse markAsRead(UUID notificationId) {
        DashboardNotification notification = this.dashboardNotificationRepository
                .findByIdAndRecipientUser_Id(notificationId, currentUserProvider.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (notification.getReadAt() == null) {
            notification.setReadAt(Instant.now());
            notification = this.dashboardNotificationRepository.save(notification);
        }

        return mapToViewResponse(notification);
    }

    @Transactional
    public void createNotificationForRequestCreated(ModelGigRequestCreatedEvent event) {
        User recipient = findUser(event.modelUserId());
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(recipient);
        notification.setType(DashboardNotificationType.MODEL_REQUEST);
        notification.setTitle("New model request");
        notification.setMessage(event.brandName() + " sent you a request for " + event.availableFor().name());
        notification.setReferenceType("MODEL_GIG_REQUEST");
        notification.setReferenceId(event.requestId());
        notification.setReferenceCode(event.requestNumber());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForRequestAccepted(ModelGigRequestAcceptedEvent event) {
        User recipient = findUser(event.brandUserId());
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(recipient);
        notification.setType(DashboardNotificationType.AGREEMENT);
        notification.setTitle("Model accepted your request");
        notification.setMessage("Agreement " + event.agreementNumber() + " has started.");
        notification.setReferenceType("MODEL_AGREEMENT");
        notification.setReferenceId(event.agreementId());
        notification.setReferenceCode(event.agreementNumber());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForRequestRejected(ModelGigRequestRejectedEvent event) {
        User recipient = findUser(event.brandUserId());
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(recipient);
        notification.setType(DashboardNotificationType.MODEL_REQUEST);
        notification.setTitle("Model rejected your request");
        notification.setMessage(
                event.rejectionReason() == null || event.rejectionReason().isBlank()
                        ? "Your request " + event.requestNumber() + " was rejected."
                        : "Your request " + event.requestNumber() + " was rejected. Reason: " + event.rejectionReason()
        );
        notification.setReferenceType("MODEL_GIG_REQUEST");
        notification.setReferenceId(event.requestId());
        notification.setReferenceCode(event.requestNumber());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
    }

    private User findUser(UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private DashboardNotificationViewResponse mapToViewResponse(DashboardNotification notification) {
        return new DashboardNotificationViewResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getReferenceType(),
                notification.getReferenceId(),
                notification.getReferenceCode(),
                notification.getActionUrl(),
                notification.getReadAt() != null,
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }

    private DashboardNotificationTypeCountResponse mapTypeCount(DashboardNotificationTypeCountRow row) {
        return new DashboardNotificationTypeCountResponse(row.type(), row.count());
    }
}
