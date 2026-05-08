package org.stylehub.backend.e_commerce.order.lisenter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.order.event.OrderCreationEvent;
import org.stylehub.backend.e_commerce.order.payment.PaymentService;
import org.stylehub.backend.e_commerce.order.payment.entity.Payment;
import org.stylehub.backend.e_commerce.platform.config.rabbitmq.RabbitMqNames;

@Component
@RequiredArgsConstructor
public class OrderLisenter {

    private final static Logger log = LoggerFactory.getLogger(OrderLisenter.class);
    private final PaymentService paymentService;

    @Transactional
    @RabbitListener(queues = {RabbitMqNames.ORDER_CREATED_QUEUE})
    public void orderCreationListener(OrderCreationEvent orderCreationEvent) {
        log.info("Order created event received for orderId={} and start creating pending payment",
                orderCreationEvent.orderId());
        Payment savedPayment=this.paymentService.createPayment(orderCreationEvent);
        log.info("payment created successfully payment={}",savedPayment);
        // notification will created
    }
}
