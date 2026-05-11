package org.stylehub.backend.e_commerce.platform.config.rabbitmq.notification;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;

@Configuration
public class NotificationRabbitQueues {

    @Bean
    public Queue modelGigRequestCreatedNotificationQueue() {
        return new Queue(RabbitMqNames.MODEL_GIG_REQUEST_CREATED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding modelGigRequestCreatedNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(modelGigRequestCreatedNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.MODEL_GIG_REQUEST_CREATED_ROUTING_KEY);
    }

    @Bean
    public Queue modelGigRequestAcceptedNotificationQueue() {
        return new Queue(RabbitMqNames.MODEL_GIG_REQUEST_ACCEPTED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding modelGigRequestAcceptedNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(modelGigRequestAcceptedNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.MODEL_GIG_REQUEST_ACCEPTED_ROUTING_KEY);
    }

    @Bean
    public Queue modelGigRequestRejectedNotificationQueue() {
        return new Queue(RabbitMqNames.MODEL_GIG_REQUEST_REJECTED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding modelGigRequestRejectedNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(modelGigRequestRejectedNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.MODEL_GIG_REQUEST_REJECTED_ROUTING_KEY);
    }
}
