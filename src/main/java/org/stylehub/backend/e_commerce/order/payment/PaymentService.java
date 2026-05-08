package org.stylehub.backend.e_commerce.order.payment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.event.OrderCreationEvent;
import org.stylehub.backend.e_commerce.order.payment.entity.Payment;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentMethod;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;
import org.stylehub.backend.e_commerce.order.payment.repository.PaymentRepository;
import org.stylehub.backend.e_commerce.order.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Payment createPayment(OrderCreationEvent orderCreationEvent) {
        Order order =orderRepository.findById(orderCreationEvent.orderId())
                .orElseThrow(() -> new RuntimeException("Order Not Found"));
        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setOrder(order);
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setAmount(orderCreationEvent.totalAmount());
        payment.setProvider("Stripe");
        return this.paymentRepository.save(payment);
    }
}
