package org.stylehub.backend.e_commerce.order.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.order.payment.entity.Payment;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findTopByOrder_IdAndPaymentStatusOrderByCreatedAtDesc(UUID id, PaymentStatus paymentStatus);
}
