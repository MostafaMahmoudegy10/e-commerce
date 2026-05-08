package org.stylehub.backend.e_commerce.order.publisher;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.stylehub.backend.e_commerce.order.event.OrderCreationEvent;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;

@Component
@RequiredArgsConstructor
public class OrderPublisherEvents {

    private final RabbitTemplate rabbitTemplate;
    private final static Logger log = LoggerFactory.getLogger(OrderPublisherEvents.class);


    public void publishOrderCreated(OrderCreationEvent orderCreationEvent) {
        log.info("Publishing order created event for orderId={}", orderCreationEvent.orderId());
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.ORDER_CREATED_ROUTING_KEY,
                orderCreationEvent
        );
    }
}
