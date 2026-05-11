package org.stylehub.backend.e_commerce.model.gig.dto;

import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.profile.enums.AvailableFor;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GigAgreementViewResponse(
        UUID agreementId,
        String agreementNumber,
        UUID requestId,
        String requestNumber,
        UUID brandId,
        String brandName,
        String brandImageUrl,
        UUID modelProfileId,
        String modelName,
        String modelEmail,
        AvailableFor availableFor,
        String title,
        String description,
        BigDecimal agreedPrice,
        Instant deadline,
        String location,
        AgreementStatus agreementStatus,
        PaymentStatus paymentStatus,
        Instant createdAt,
        Instant acceptedAt,
        Instant deliveredAt,
        Instant completedAt
) {
}
