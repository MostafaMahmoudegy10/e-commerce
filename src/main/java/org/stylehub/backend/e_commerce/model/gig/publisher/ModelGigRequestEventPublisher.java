package org.stylehub.backend.e_commerce.model.gig.publisher;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestAcceptedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestCreatedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestRejectedEvent;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;

@Component
@RequiredArgsConstructor
public class ModelGigRequestEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelGigRequestEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public void publishRequestCreated(ModelGigRequestCreatedEvent event) {
        LOGGER.info("Publishing model gig request created event for requestId={}", event.requestId());
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.MODEL_GIG_REQUEST_CREATED_ROUTING_KEY,
                event
        );
    }

    public void publishRequestAccepted(ModelGigRequestAcceptedEvent event) {
        LOGGER.info("Publishing model gig request accepted event for requestId={}", event.requestId());
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.MODEL_GIG_REQUEST_ACCEPTED_ROUTING_KEY,
                event
        );
    }

    public void publishRequestRejected(ModelGigRequestRejectedEvent event) {
        LOGGER.info("Publishing model gig request rejected event for requestId={}", event.requestId());
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.MODEL_GIG_REQUEST_REJECTED_ROUTING_KEY,
                event
        );
    }
}
