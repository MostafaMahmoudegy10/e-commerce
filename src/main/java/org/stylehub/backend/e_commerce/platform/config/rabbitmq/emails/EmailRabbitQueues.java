package org.stylehub.backend.e_commerce.platform.config.rabbitmq.emails;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;

@Configuration
public class EmailRabbitQueues {

    @Bean
    public TopicExchange   ecommerceTopicExchange(){
        return new TopicExchange(RabbitMqNames.ECOMMERCE_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue stockInsufficientQueue() {
        return new Queue(RabbitMqNames.STOCK_INSUFFICIENT_QUEUE);
    }
    @Bean
    public Binding stockInsufficientBinding() {
        return BindingBuilder.bind(stockInsufficientQueue()).to(ecommerceTopicExchange()).with(RabbitMqNames.STOCK_INSUFFICIENT_ROUTING_KEY);
    }





}
