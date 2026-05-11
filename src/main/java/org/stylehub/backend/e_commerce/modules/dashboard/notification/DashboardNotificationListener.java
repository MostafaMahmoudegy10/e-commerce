package org.stylehub.backend.e_commerce.modules.dashboard.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestAcceptedEvent;
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
}
