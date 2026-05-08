package org.stylehub.backend.e_commerce.order.publisher;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.order.event.OrderCreationEvent;
import org.stylehub.backend.e_commerce.order.payment.PaymentService;
import org.stylehub.backend.e_commerce.order.payment.entity.Payment;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;

@Component
@RequiredArgsConstructor
public class OrderPublisherEvents {

    private final RabbitTemplate rabbitTemplate;
    private final static Logger log = LoggerFactory.getLogger(OrderPublisherEvents.class);

    @Async
    public void orderCreated(OrderCreationEvent  orderCreationEvent) {
        log.info("Order Created Event Received & starting of publishing the event ");
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.ORDER_CREATED_ROUTING_KEY,
                orderCreationEvent
        );
    }
}
