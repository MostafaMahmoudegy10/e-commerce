package org.stylehub.backend.e_commerce.platform.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQCustomerCreationConfiguration {

    private static final String EXCHANGE_NAME = "social_media_exchange";
    private static final String CUSTOMER_CREATION_USER_SERVICE_QUEUE =
            "customer.created.user.service.q";
    private static final String CUSTOMER_CREATION_KEY="social.user.profile-completed";

    @Bean
    public Queue customerCreationUserServiceQueue() {
        return new Queue(CUSTOMER_CREATION_USER_SERVICE_QUEUE, true);
    }
    @Bean
    public TopicExchange customerCreationUserServiceExchange() {
        return new TopicExchange(EXCHANGE_NAME,true,false);
    }

    @Bean
    public Binding customerCreationUserServiceBinding() {
        return BindingBuilder.bind(customerCreationUserServiceQueue()).to(customerCreationUserServiceExchange()).with(CUSTOMER_CREATION_KEY);
    }
}
