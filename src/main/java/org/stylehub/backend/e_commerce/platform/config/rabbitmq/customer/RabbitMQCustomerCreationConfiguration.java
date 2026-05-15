package org.stylehub.backend.e_commerce.platform.config.rabbitmq.customer;

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
    private static final String CUSTOMER_UPDATED_USER_SERVICE_QUEUE =
            "customer.updated.user.service.q";
    private static final String CUSTOMER_DELETED_USER_SERVICE_QUEUE =
            "customer.deleted.user.service.q";
    private static final String CUSTOMER_CREATION_KEY="social.user.profile-completed";
    private static final String CUSTOMER_UPDATED_KEY = "social.user.profile-updated";
    private static final String CUSTOMER_DELETED_KEY = "social.user.profile-deleted";

    @Bean
    public Queue customerCreationUserServiceQueue() {
        return new Queue(CUSTOMER_CREATION_USER_SERVICE_QUEUE, true);
    }

    @Bean
    public Queue customerUpdatedUserServiceQueue() {
        return new Queue(CUSTOMER_UPDATED_USER_SERVICE_QUEUE, true);
    }

    @Bean
    public Queue customerDeletedUserServiceQueue() {
        return new Queue(CUSTOMER_DELETED_USER_SERVICE_QUEUE, true);
    }

    @Bean
    public TopicExchange customerCreationUserServiceExchange() {
        return new TopicExchange(EXCHANGE_NAME,true,false);
    }

    @Bean
    public Binding customerCreationUserServiceBinding() {
        return BindingBuilder.bind(customerCreationUserServiceQueue()).to(customerCreationUserServiceExchange()).with(CUSTOMER_CREATION_KEY);
    }

    @Bean
    public Binding customerUpdatedUserServiceBinding() {
        return BindingBuilder.bind(customerUpdatedUserServiceQueue()).to(customerCreationUserServiceExchange()).with(CUSTOMER_UPDATED_KEY);
    }

    @Bean
    public Binding customerDeletedUserServiceBinding() {
        return BindingBuilder.bind(customerDeletedUserServiceQueue()).to(customerCreationUserServiceExchange()).with(CUSTOMER_DELETED_KEY);
    }
}
