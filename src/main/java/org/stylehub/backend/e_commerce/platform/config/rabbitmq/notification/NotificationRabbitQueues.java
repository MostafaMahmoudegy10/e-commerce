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
    public Queue orderCreatedNotificationQueue() {
        return new Queue(RabbitMqNames.ORDER_CREATED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding orderCreatedNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(orderCreatedNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Queue orderPaidNotificationQueue() {
        return new Queue(RabbitMqNames.ORDER_PAID_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding orderPaidNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(orderPaidNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.ORDER_PAID_ROUTING_KEY);
    }

    @Bean
    public Queue orderShippedNotificationQueue() {
        return new Queue(RabbitMqNames.ORDER_SHIPPED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding orderShippedNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(orderShippedNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.ORDER_SHIPPED_ROUTING_KEY);
    }

    @Bean
    public Queue orderDeliveredNotificationQueue() {
        return new Queue(RabbitMqNames.ORDER_DELIVERED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding orderDeliveredNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(orderDeliveredNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.ORDER_DELIVERED_ROUTING_KEY);
    }

    @Bean
    public Queue inventoryLowStockNotificationQueue() {
        return new Queue(RabbitMqNames.INVENTORY_LOW_STOCK_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding inventoryLowStockNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(inventoryLowStockNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.INVENTORY_LOW_STOCK_ROUTING_KEY);
    }

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

    @Bean
    public Queue modelGigRequestCancelledNotificationQueue() {
        return new Queue(RabbitMqNames.MODEL_GIG_REQUEST_CANCELLED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding modelGigRequestCancelledNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(modelGigRequestCancelledNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.MODEL_GIG_REQUEST_CANCELLED_ROUTING_KEY);
    }

    @Bean
    public Queue modelAgreementSubmittedNotificationQueue() {
        return new Queue(RabbitMqNames.MODEL_AGREEMENT_SUBMITTED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding modelAgreementSubmittedNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(modelAgreementSubmittedNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.MODEL_AGREEMENT_SUBMITTED_ROUTING_KEY);
    }

    @Bean
    public Queue modelAgreementRevisionRequestedNotificationQueue() {
        return new Queue(RabbitMqNames.MODEL_AGREEMENT_REVISION_REQUESTED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding modelAgreementRevisionRequestedNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(modelAgreementRevisionRequestedNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.MODEL_AGREEMENT_REVISION_REQUESTED_ROUTING_KEY);
    }

    @Bean
    public Queue modelAgreementApprovedNotificationQueue() {
        return new Queue(RabbitMqNames.MODEL_AGREEMENT_APPROVED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding modelAgreementApprovedNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(modelAgreementApprovedNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.MODEL_AGREEMENT_APPROVED_ROUTING_KEY);
    }

    @Bean
    public Queue modelAgreementPaymentSucceededNotificationQueue() {
        return new Queue(RabbitMqNames.MODEL_AGREEMENT_PAYMENT_SUCCEEDED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding modelAgreementPaymentSucceededNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(modelAgreementPaymentSucceededNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.MODEL_AGREEMENT_PAYMENT_SUCCEEDED_ROUTING_KEY);
    }

    @Bean
    public Queue modelAgreementPaymentFailedNotificationQueue() {
        return new Queue(RabbitMqNames.MODEL_AGREEMENT_PAYMENT_FAILED_NOTIFICATION_QUEUE);
    }

    @Bean
    public Binding modelAgreementPaymentFailedNotificationBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(modelAgreementPaymentFailedNotificationQueue())
                .to(exchange)
                .with(RabbitMqNames.MODEL_AGREEMENT_PAYMENT_FAILED_ROUTING_KEY);
    }
}
