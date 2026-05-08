package org.stylehub.backend.e_commerce.platform.config.rabbitmq.order;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;

@Configuration
public class OrderBindingQueues {

    // Create The Queue We will send the event on it "order created"
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(RabbitMqNames.ORDER_CREATED_QUEUE);
    }
    //create binding for the order queue
    @Bean
    public Binding orderCreatedBinding(@Qualifier("ecommerceTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(orderCreatedQueue()).to(exchange).with(RabbitMqNames.ORDER_CREATED_ROUTING_KEY);
    }
}
