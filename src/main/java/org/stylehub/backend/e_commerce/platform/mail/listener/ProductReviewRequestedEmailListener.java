package org.stylehub.backend.e_commerce.platform.mail.listener;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;
import org.stylehub.backend.e_commerce.platform.mail.EmailService;
import org.stylehub.backend.e_commerce.platform.mail.events.ProductReviewRequestedEmailEvent;
import org.stylehub.backend.e_commerce.platform.mail.template.ProductReviewRequestEmailTemplate;

@Component
@RequiredArgsConstructor
public class ProductReviewRequestedEmailListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductReviewRequestedEmailListener.class);

    private final EmailService emailService;
    private final ProductReviewRequestEmailTemplate productReviewRequestEmailTemplate;

    @RabbitListener(queues = RabbitMqNames.PRODUCT_REVIEW_REQUESTED_EMAIL_QUEUE)
    public void onProductReviewRequested(ProductReviewRequestedEmailEvent event) {
        LOGGER.info(
                "Received product review email request for orderNumber={}, customerEmail={}",
                event.orderNumber(),
                event.customerEmail()
        );

        String htmlBody = this.productReviewRequestEmailTemplate.buildTemplate(event);
        this.emailService.sendHtmlEmail(
                event.customerEmail(),
                "Review your StyleHub order - " + event.orderNumber(),
                htmlBody
        );

        LOGGER.info(
                "Product review request email sent for orderNumber={} to customerEmail={}",
                event.orderNumber(),
                event.customerEmail()
        );
    }
}
