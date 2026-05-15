package org.stylehub.backend.e_commerce.modules.dashboard.notification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementApprovedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementPaymentFailedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementPaymentSucceededEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementRevisionRequestedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementSubmittedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestAcceptedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestCancelledEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestCreatedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestRejectedEvent;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationReadAllResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationStatsResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationTypeCountResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationTypeCountRow;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationViewResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotification;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotificationType;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.repository.DashboardNotificationRepository;
import org.stylehub.backend.e_commerce.order.event.InventoryLowStockEvent;
import org.stylehub.backend.e_commerce.order.event.OrderCreationEvent;
import org.stylehub.backend.e_commerce.order.event.OrderLifecycleEvent;
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
    public DashboardNotificationReadAllResponse markAllAsRead() {
        UUID userId = currentUserProvider.getUserId();
        List<DashboardNotification> unreadNotifications =
                this.dashboardNotificationRepository.findAllByRecipientUser_IdAndReadAtIsNull(userId);

        Instant now = Instant.now();
        unreadNotifications.forEach(notification -> notification.setReadAt(now));
        this.dashboardNotificationRepository.saveAll(unreadNotifications);

        return new DashboardNotificationReadAllResponse(
                unreadNotifications.size(),
                this.dashboardNotificationRepository.countByRecipientUser_Id(userId),
                this.dashboardNotificationRepository.countByRecipientUser_IdAndReadAtIsNull(userId),
                this.dashboardNotificationRepository.countByRecipientUser_IdAndReadAtIsNotNull(userId)
        );
    }

    @Transactional
    public void createNotificationForOrderCreated(OrderCreationEvent event) {
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(findUser(event.brandUserId()));
        notification.setType(DashboardNotificationType.ORDER);
        notification.setTitle("New order received");
        notification.setMessage("Order " + event.orderNumber() + " was created by " + event.customerEmail() + ".");
        notification.setReferenceType("ORDER");
        notification.setReferenceId(event.orderId());
        notification.setReferenceCode(event.orderNumber());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForOrderPaid(OrderLifecycleEvent event) {
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(findUser(event.brandUserId()));
        notification.setType(DashboardNotificationType.PAYMENT);
        notification.setTitle("Payment received");
        notification.setMessage("Payment for order " + event.orderNumber() + " was completed successfully.");
        notification.setReferenceType("ORDER_PAYMENT");
        notification.setReferenceId(event.orderId());
        notification.setReferenceCode(event.orderNumber());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForOrderShipped(OrderLifecycleEvent event) {
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(findUser(event.brandUserId()));
        notification.setType(DashboardNotificationType.ORDER);
        notification.setTitle("Order shipped");
        notification.setMessage("Order " + event.orderNumber() + " moved to shipped and is now on the way to the customer.");
        notification.setReferenceType("ORDER");
        notification.setReferenceId(event.orderId());
        notification.setReferenceCode(event.orderNumber());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForOrderDelivered(OrderLifecycleEvent event) {
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(findUser(event.brandUserId()));
        notification.setType(DashboardNotificationType.ORDER);
        notification.setTitle("Order delivered");
        notification.setMessage("Order " + event.orderNumber() + " was marked as delivered.");
        notification.setReferenceType("ORDER");
        notification.setReferenceId(event.orderId());
        notification.setReferenceCode(event.orderNumber());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForInventoryLowStock(InventoryLowStockEvent event) {
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(findUser(event.brandUserId()));
        notification.setType(DashboardNotificationType.INVENTORY);
        notification.setTitle("Low stock on " + event.productName());
        notification.setMessage("SKU " + event.sku() + " dropped to " + event.remainingStock() + " units.");
        notification.setReferenceType("PRODUCT");
        notification.setReferenceId(event.productId());
        notification.setReferenceCode(event.sku());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
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

    @Transactional
    public void createNotificationForRequestCancelled(ModelGigRequestCancelledEvent event) {
        User recipient = findUser(event.modelUserId());
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(recipient);
        notification.setType(DashboardNotificationType.MODEL_REQUEST);
        notification.setTitle("Request cancelled");
        notification.setMessage(event.brandName() + " cancelled request " + event.requestNumber() + ".");
        notification.setReferenceType("MODEL_GIG_REQUEST");
        notification.setReferenceId(event.requestId());
        notification.setReferenceCode(event.requestNumber());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForAgreementSubmitted(ModelAgreementSubmittedEvent event) {
        User recipient = findUser(event.brandUserId());
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(recipient);
        notification.setType(DashboardNotificationType.SUBMISSION);
        notification.setTitle("New submission received");
        notification.setMessage(event.modelName() + " submitted files for agreement " + event.agreementNumber() + ".");
        notification.setReferenceType("MODEL_AGREEMENT_SUBMISSION");
        notification.setReferenceId(event.submissionId());
        notification.setReferenceCode(event.agreementNumber());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForRevisionRequested(ModelAgreementRevisionRequestedEvent event) {
        User recipient = findUser(event.modelUserId());
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(recipient);
        notification.setType(DashboardNotificationType.SUBMISSION);
        notification.setTitle("Revision requested");
        notification.setMessage(
                event.feedback() == null || event.feedback().isBlank()
                        ? "A revision was requested for agreement " + event.agreementNumber() + "."
                        : "A revision was requested for agreement " + event.agreementNumber() + ". Feedback: " + event.feedback()
        );
        notification.setReferenceType("MODEL_AGREEMENT_SUBMISSION");
        notification.setReferenceId(event.submissionId());
        notification.setReferenceCode(event.agreementNumber());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForAgreementApproved(ModelAgreementApprovedEvent event) {
        User recipient = findUser(event.modelUserId());
        DashboardNotification notification = new DashboardNotification();
        notification.setRecipientUser(recipient);
        notification.setType(DashboardNotificationType.PAYMENT);
        notification.setTitle("Submission approved");
        notification.setMessage("Your submission for agreement " + event.agreementNumber() + " was approved. Payment is now pending.");
        notification.setReferenceType("MODEL_AGREEMENT");
        notification.setReferenceId(event.agreementId());
        notification.setReferenceCode(event.agreementNumber());
        notification.setActionUrl(null);
        this.dashboardNotificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForPaymentSucceeded(ModelAgreementPaymentSucceededEvent event) {
        DashboardNotification brandNotification = new DashboardNotification();
        brandNotification.setRecipientUser(findUser(event.brandUserId()));
        brandNotification.setType(DashboardNotificationType.PAYMENT);
        brandNotification.setTitle("Payment completed");
        brandNotification.setMessage("Payment for agreement " + event.agreementNumber() + " was completed successfully. You can now review the model.");
        brandNotification.setReferenceType("MODEL_AGREEMENT_PAYMENT");
        brandNotification.setReferenceId(event.paymentId());
        brandNotification.setReferenceCode(event.agreementNumber());
        brandNotification.setActionUrl(null);

        DashboardNotification modelNotification = new DashboardNotification();
        modelNotification.setRecipientUser(findUser(event.modelUserId()));
        modelNotification.setType(DashboardNotificationType.PAYMENT);
        modelNotification.setTitle("Payment received");
        modelNotification.setMessage("Payment for agreement " + event.agreementNumber() + " was completed successfully.");
        modelNotification.setReferenceType("MODEL_AGREEMENT_PAYMENT");
        modelNotification.setReferenceId(event.paymentId());
        modelNotification.setReferenceCode(event.agreementNumber());
        modelNotification.setActionUrl(null);

        this.dashboardNotificationRepository.save(brandNotification);
        this.dashboardNotificationRepository.save(modelNotification);
    }

    @Transactional
    public void createNotificationForPaymentFailed(ModelAgreementPaymentFailedEvent event) {
        DashboardNotification brandNotification = new DashboardNotification();
        brandNotification.setRecipientUser(findUser(event.brandUserId()));
        brandNotification.setType(DashboardNotificationType.PAYMENT);
        brandNotification.setTitle("Payment failed");
        brandNotification.setMessage(
                event.failureReason() == null || event.failureReason().isBlank()
                        ? "Payment for agreement " + event.agreementNumber() + " failed."
                        : "Payment for agreement " + event.agreementNumber() + " failed. Reason: " + event.failureReason()
        );
        brandNotification.setReferenceType("MODEL_AGREEMENT_PAYMENT");
        brandNotification.setReferenceId(event.paymentId());
        brandNotification.setReferenceCode(event.agreementNumber());
        brandNotification.setActionUrl(null);

        DashboardNotification modelNotification = new DashboardNotification();
        modelNotification.setRecipientUser(findUser(event.modelUserId()));
        modelNotification.setType(DashboardNotificationType.PAYMENT);
        modelNotification.setTitle("Brand payment failed");
        modelNotification.setMessage(
                event.failureReason() == null || event.failureReason().isBlank()
                        ? "Payment for agreement " + event.agreementNumber() + " failed and may be retried."
                        : "Payment for agreement " + event.agreementNumber() + " failed. Reason: " + event.failureReason()
        );
        modelNotification.setReferenceType("MODEL_AGREEMENT_PAYMENT");
        modelNotification.setReferenceId(event.paymentId());
        modelNotification.setReferenceCode(event.agreementNumber());
        modelNotification.setActionUrl(null);

        this.dashboardNotificationRepository.save(brandNotification);
        this.dashboardNotificationRepository.save(modelNotification);
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
