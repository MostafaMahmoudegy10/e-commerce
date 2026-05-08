package org.stylehub.backend.e_commerce.order.payment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.cart.entity.CartStatus;
import org.stylehub.backend.e_commerce.cart.repository.CartRepository;
import org.stylehub.backend.e_commerce.customer.dto.payment.PaymentResponse;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.customer.profile.service.CustomerProfileService;
import org.stylehub.backend.e_commerce.customer.service.CustomerCartService;
import org.stylehub.backend.e_commerce.order.entity.Order;
import org.stylehub.backend.e_commerce.order.entity.OrderStatus;
import org.stylehub.backend.e_commerce.order.event.OrderCreationEvent;
import org.stylehub.backend.e_commerce.order.item.entity.OrderItem;
import org.stylehub.backend.e_commerce.order.item.repoistory.OrderItemRepository;
import org.stylehub.backend.e_commerce.order.payment.entity.Payment;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentMethod;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;
import org.stylehub.backend.e_commerce.order.payment.repository.PaymentRepository;
import org.stylehub.backend.e_commerce.order.repository.OrderRepository;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;
import org.stylehub.backend.e_commerce.product.color.variant.repository.ProductVariantRepository;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartRepository cartRepository;
    private final CustomerProfileService customerProfileService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public Payment createPayment(OrderCreationEvent event) {
        return paymentRepository.findByOrder_Id(event.orderId())
                .orElseGet(() -> {
                    Order order = orderRepository.findById(event.orderId())
                            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
                    return createPayment(order);
                });
    }

    @Transactional
    public Payment createPayment(Order order) {
        return paymentRepository.findByOrder_Id(order.getId())
                .orElseGet(() -> {
                    Payment payment = new Payment();
                    payment.setPaymentStatus(PaymentStatus.PENDING);
                    payment.setOrder(order);
                    payment.setPaymentMethod(PaymentMethod.CARD);
                    payment.setAmount(order.getTotalPrice());
                    payment.setProvider("FAKE");
                    return paymentRepository.save(payment);
                });
    }

    @Transactional
    public PaymentResponse successPayment(UUID orderId) {
        CustomerProfile customer = customerProfileService
                .findCustomerProfileByExternalUserId(currentUserProvider.externalId());

        Payment payment = paymentRepository.findTopByOrder_IdAndPaymentStatusOrderByCreatedAtDesc(orderId,PaymentStatus.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not pending");
        }

        Order order = payment.getOrder();

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("This payment does not belong to current customer");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not pending");
        }

        List<OrderItem> orderItems =
                orderItemRepository.findAllByOrder_Id(order.getId());

        validateStock(orderItems);
        decreaseStock(orderItems);

        payment.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.PAID);

        Cart cart = order.getCart();
        cart.setCartStatus(CartStatus.CHECKED_OUT);

        paymentRepository.save(payment);
        orderRepository.save(order);
        cartRepository.save(cart);

        return new PaymentResponse(
                payment.getId(),
                order.getId(),
                order.getOrderNumber(),
                payment.getPaymentStatus(),
                order.getOrderStatus(),
                "Payment completed successfully"
        );
    }

    @Transactional
    public PaymentResponse failPayment(UUID orderId) {
        CustomerProfile customer = customerProfileService
                .findCustomerProfileByExternalUserId(currentUserProvider.externalId());

        Payment payment = paymentRepository.findTopByOrder_IdAndPaymentStatusOrderByCreatedAtDesc(orderId,PaymentStatus.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not pending");
        }

        Order order = payment.getOrder();

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("This payment does not belong to current customer");
        }

        payment.setPaymentStatus(PaymentStatus.FAILED);
        order.setOrderStatus(OrderStatus.CANCELLED);


        paymentRepository.save(payment);
        orderRepository.save(order);

        return new PaymentResponse(
                payment.getId(),
                order.getId(),
                order.getOrderNumber(),
                payment.getPaymentStatus(),
                order.getOrderStatus(),
                "Payment failed"
        );
    }

    private void validateStock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            ProductVariant variant = item.getVariant();

            if (item.getOrderQuantity() > variant.getStock()) {
                throw new IllegalStateException(
                        "Not enough stock for sku="
                                + variant.getSku()
                                + ", available="
                                + variant.getStock()
                );
            }
        }
    }

    private void decreaseStock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            ProductVariant variant = item.getVariant();

            variant.setStock(variant.getStock() - item.getOrderQuantity());

            productVariantRepository.save(variant);
        }
    }
}
