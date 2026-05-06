package org.stylehub.backend.e_commerce.platform.mail.listener;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;
import org.stylehub.backend.e_commerce.platform.mail.EmailService;
import org.stylehub.backend.e_commerce.platform.mail.events.InsufficientStockRequestedEvent;
import org.stylehub.backend.e_commerce.platform.mail.template.InsufficientEmailTemplate;

@Component
@RequiredArgsConstructor
public class StockInsufficientEmailListener {

    private final EmailService emailService;

    private final static Logger LOGGER = LoggerFactory.getLogger(StockInsufficientEmailListener.class);

    @RabbitListener(queues = {RabbitMqNames.STOCK_INSUFFICIENT_QUEUE})
    public  void handleStockInsufficientStockListener (InsufficientStockRequestedEvent event) {
        LOGGER.info(
                "Received insufficient stock event for sku={}, brandEmail={}",
                event.sku(),
                event.brandOwnerEmail()
        );
       String htmlBody= InsufficientEmailTemplate.buildInsufficientEmailTemplate(event,event.brandOwnerEmail());
       this.emailService.sendHtmlEmail(event.brandOwnerEmail(),
               "StyleHub Stock Alert - " + event.productName()
               ,htmlBody);
        LOGGER.info(
                "Stock alert email sent to brandEmail={}",
                event.brandOwnerEmail()
        );
    }
}
