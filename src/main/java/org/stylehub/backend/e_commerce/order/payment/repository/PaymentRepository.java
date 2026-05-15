package org.stylehub.backend.e_commerce.order.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.order.payment.entity.Payment;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findTopByOrder_IdAndPaymentStatusOrderByCreatedAtDesc(UUID id, PaymentStatus paymentStatus);

    Optional<Payment> findByOrder_Id(UUID id);

    List<Payment> findByOrder_IdIn(List<UUID> orderIds);

    @Query("""
            select sum(p.amount)
            from Payment p
            join p.order o
            join o.brand b
            join b.user bu
            where bu.externalUserId = :externalId
              and p.paymentStatus = :paymentStatus
            """)
    BigDecimal sumRevenueByBrandExternalIdAndPaymentStatus(
            @Param("externalId") String externalId,
            @Param("paymentStatus") PaymentStatus paymentStatus
    );
}
