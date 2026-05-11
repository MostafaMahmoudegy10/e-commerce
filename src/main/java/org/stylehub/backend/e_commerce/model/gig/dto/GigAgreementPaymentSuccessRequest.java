package org.stylehub.backend.e_commerce.model.gig.dto;

import org.stylehub.backend.e_commerce.order.payment.entity.PaymentMethod;

public record GigAgreementPaymentSuccessRequest(
        PaymentMethod paymentMethod,
        String provider,
        String providerPaymentId,
        String transactionReference
) {
}
