package org.stylehub.backend.e_commerce.platform.mail.listener;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;
import org.stylehub.backend.e_commerce.platform.mail.EmailService;
import org.stylehub.backend.e_commerce.platform.mail.events.ModelReviewRequestedEmailEvent;
import org.stylehub.backend.e_commerce.platform.mail.template.ModelReviewRequestEmailTemplate;

@Component
@RequiredArgsConstructor
public class ModelReviewRequestedEmailListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelReviewRequestedEmailListener.class);

    private final EmailService emailService;
    private final ModelReviewRequestEmailTemplate modelReviewRequestEmailTemplate;

    @RabbitListener(queues = RabbitMqNames.MODEL_REVIEW_REQUESTED_EMAIL_QUEUE)
    public void onModelReviewRequested(ModelReviewRequestedEmailEvent event) {
        LOGGER.info(
                "Received model review email request for agreementNumber={}, brandEmail={}",
                event.agreementNumber(),
                event.brandEmail()
        );

        String htmlBody = this.modelReviewRequestEmailTemplate.buildTemplate(event);
        this.emailService.sendHtmlEmail(
                event.brandEmail(),
                "Review your model on StyleHub - " + event.agreementNumber(),
                htmlBody
        );

        LOGGER.info(
                "Model review request email sent for agreementNumber={} to brandEmail={}",
                event.agreementNumber(),
                event.brandEmail()
        );
    }
}
