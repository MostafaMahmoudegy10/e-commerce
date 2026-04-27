package org.stylehub.backend.e_commerce.platform.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQBrandCreationConfiguration {

    private static final String SOCIAL_MEDIA_EXCHANGE = "social_media_exchange";
    private static final String BRAND_CREATED_USER_SERVICE_QUEUE = "brand.created.user.service.q";
    private static final String BRAND_CREATED_KEY="social.brand.profile-completed";

    @Bean
    public Queue brandCreatedUserServiceQueue() {
        return new Queue(BRAND_CREATED_USER_SERVICE_QUEUE, true);
    }

    @Bean
    public TopicExchange brandCreatedUserServiceExchange() {
        return new TopicExchange(SOCIAL_MEDIA_EXCHANGE,true,false);
    }

    @Bean
    public Binding brandCreatedUserServiceBinding() {
        return BindingBuilder.bind(brandCreatedUserServiceQueue()).to(brandCreatedUserServiceExchange()).with(BRAND_CREATED_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
