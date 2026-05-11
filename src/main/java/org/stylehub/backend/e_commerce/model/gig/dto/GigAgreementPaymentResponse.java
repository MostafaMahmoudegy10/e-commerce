package org.stylehub.backend.e_commerce.model.gig.dto;

import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentMethod;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GigAgreementPaymentResponse(
        UUID paymentId,
        UUID agreementId,
        String agreementNumber,
        BigDecimal amount,
        PaymentStatus paymentStatus,
        AgreementStatus agreementStatus,
        PaymentMethod paymentMethod,
        String provider,
        String providerPaymentId,
        String transactionReference,
        String failureReason,
        Instant paidAt,
        Instant completedAt,
        String message
) {
}
