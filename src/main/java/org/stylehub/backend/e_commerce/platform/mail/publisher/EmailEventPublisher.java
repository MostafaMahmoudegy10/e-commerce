package org.stylehub.backend.e_commerce.platform.mail.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;
import org.stylehub.backend.e_commerce.platform.mail.events.InsufficientStockRequestedEvent;
import org.stylehub.backend.e_commerce.platform.mail.events.ModelReviewRequestedEmailEvent;
import org.stylehub.backend.e_commerce.platform.mail.events.ProductReviewRequestedEmailEvent;

@Service
@RequiredArgsConstructor
public class EmailEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Async
    public  void publishInsufficientStockRequested(
            InsufficientStockRequestedEvent event
    ){
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.STOCK_INSUFFICIENT_ROUTING_KEY,
                event
        );
    }

    @Async
    public void publishModelReviewRequested(ModelReviewRequestedEmailEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.MODEL_REVIEW_REQUESTED_EMAIL_ROUTING_KEY,
                event
        );
    }

    @Async
    public void publishProductReviewRequested(ProductReviewRequestedEmailEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.PRODUCT_REVIEW_REQUESTED_EMAIL_ROUTING_KEY,
                event
        );
    }
}
