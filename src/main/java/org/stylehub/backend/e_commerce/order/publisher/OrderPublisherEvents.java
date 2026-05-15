package org.stylehub.backend.e_commerce.order.publisher;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.order.event.InventoryLowStockEvent;
import org.stylehub.backend.e_commerce.order.event.OrderCreationEvent;
import org.stylehub.backend.e_commerce.order.event.OrderLifecycleEvent;
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

    public void publishOrderPaid(OrderLifecycleEvent event) {
        publishOrderLifecycleEvent(event, RabbitMqNames.ORDER_PAID_ROUTING_KEY, "paid");
    }

    public void publishOrderShipped(OrderLifecycleEvent event) {
        publishOrderLifecycleEvent(event, RabbitMqNames.ORDER_SHIPPED_ROUTING_KEY, "shipped");
    }

    public void publishOrderDelivered(OrderLifecycleEvent event) {
        publishOrderLifecycleEvent(event, RabbitMqNames.ORDER_DELIVERED_ROUTING_KEY, "delivered");
    }

    public void publishInventoryLowStock(InventoryLowStockEvent event) {
        log.info("Publishing inventory low stock event for sku={}", event.sku());
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                RabbitMqNames.INVENTORY_LOW_STOCK_ROUTING_KEY,
                event
        );
    }

    private void publishOrderLifecycleEvent(OrderLifecycleEvent event, String routingKey, String action) {
        log.info("Publishing order {} event for orderId={}", action, event.orderId());
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE,
                routingKey,
                event
        );
    }
}
