package org.stylehub.backend.e_commerce.platform.mail.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;
import org.stylehub.backend.e_commerce.platform.mail.events.InsufficientStockRequestedEvent;

@Service
@RequiredArgsConstructor
public class EmailEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public  void publishInsufficientStockRequested(
            InsufficientStockRequestedEvent event
    ){
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.STOCK_INSUFFICIENT_ROUTING_KEY,
                event
        );
    }
}
