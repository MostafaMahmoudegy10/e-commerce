package org.stylehub.backend.e_commerce.modules.dashboard.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementApprovedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementPaymentFailedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementPaymentSucceededEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementRevisionRequestedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementSubmittedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestAcceptedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestCancelledEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestCreatedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestRejectedEvent;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.service.DashboardNotificationService;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;

@Component
@RequiredArgsConstructor
public class DashboardNotificationListener {

    private final DashboardNotificationService dashboardNotificationService;

    @RabbitListener(queues = RabbitMqNames.MODEL_GIG_REQUEST_CREATED_NOTIFICATION_QUEUE)
    public void onRequestCreated(ModelGigRequestCreatedEvent event) {
        this.dashboardNotificationService.createNotificationForRequestCreated(event);
    }

    @RabbitListener(queues = RabbitMqNames.MODEL_GIG_REQUEST_ACCEPTED_NOTIFICATION_QUEUE)
    public void onRequestAccepted(ModelGigRequestAcceptedEvent event) {
        this.dashboardNotificationService.createNotificationForRequestAccepted(event);
    }

    @RabbitListener(queues = RabbitMqNames.MODEL_GIG_REQUEST_REJECTED_NOTIFICATION_QUEUE)
    public void onRequestRejected(ModelGigRequestRejectedEvent event) {
        this.dashboardNotificationService.createNotificationForRequestRejected(event);
    }

    @RabbitListener(queues = RabbitMqNames.MODEL_GIG_REQUEST_CANCELLED_NOTIFICATION_QUEUE)
    public void onRequestCancelled(ModelGigRequestCancelledEvent event) {
        this.dashboardNotificationService.createNotificationForRequestCancelled(event);
    }

    @RabbitListener(queues = RabbitMqNames.MODEL_AGREEMENT_SUBMITTED_NOTIFICATION_QUEUE)
    public void onAgreementSubmitted(ModelAgreementSubmittedEvent event) {
        this.dashboardNotificationService.createNotificationForAgreementSubmitted(event);
    }

    @RabbitListener(queues = RabbitMqNames.MODEL_AGREEMENT_REVISION_REQUESTED_NOTIFICATION_QUEUE)
    public void onRevisionRequested(ModelAgreementRevisionRequestedEvent event) {
        this.dashboardNotificationService.createNotificationForRevisionRequested(event);
    }

    @RabbitListener(queues = RabbitMqNames.MODEL_AGREEMENT_APPROVED_NOTIFICATION_QUEUE)
    public void onAgreementApproved(ModelAgreementApprovedEvent event) {
        this.dashboardNotificationService.createNotificationForAgreementApproved(event);
    }

    @RabbitListener(queues = RabbitMqNames.MODEL_AGREEMENT_PAYMENT_SUCCEEDED_NOTIFICATION_QUEUE)
    public void onPaymentSucceeded(ModelAgreementPaymentSucceededEvent event) {
        this.dashboardNotificationService.createNotificationForPaymentSucceeded(event);
    }

    @RabbitListener(queues = RabbitMqNames.MODEL_AGREEMENT_PAYMENT_FAILED_NOTIFICATION_QUEUE)
    public void onPaymentFailed(ModelAgreementPaymentFailedEvent event) {
        this.dashboardNotificationService.createNotificationForPaymentFailed(event);
    }
}
