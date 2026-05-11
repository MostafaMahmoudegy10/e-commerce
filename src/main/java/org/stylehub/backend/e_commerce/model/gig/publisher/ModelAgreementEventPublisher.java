package org.stylehub.backend.e_commerce.model.gig.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementApprovedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementPaymentFailedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementPaymentSucceededEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementRevisionRequestedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementSubmittedEvent;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;

@Component
@RequiredArgsConstructor
public class ModelAgreementEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAgreementSubmitted(ModelAgreementSubmittedEvent event) {
        this.rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.MODEL_AGREEMENT_SUBMITTED_ROUTING_KEY,
                event
        );
    }

    public void publishRevisionRequested(ModelAgreementRevisionRequestedEvent event) {
        this.rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.MODEL_AGREEMENT_REVISION_REQUESTED_ROUTING_KEY,
                event
        );
    }

    public void publishAgreementApproved(ModelAgreementApprovedEvent event) {
        this.rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.MODEL_AGREEMENT_APPROVED_ROUTING_KEY,
                event
        );
    }

    public void publishPaymentSucceeded(ModelAgreementPaymentSucceededEvent event) {
        this.rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.MODEL_AGREEMENT_PAYMENT_SUCCEEDED_ROUTING_KEY,
                event
        );
    }

    public void publishPaymentFailed(ModelAgreementPaymentFailedEvent event) {
        this.rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.MODEL_AGREEMENT_PAYMENT_FAILED_ROUTING_KEY,
                event
        );
    }
}
