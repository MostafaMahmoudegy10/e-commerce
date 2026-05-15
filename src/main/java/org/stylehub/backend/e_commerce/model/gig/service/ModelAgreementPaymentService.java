package org.stylehub.backend.e_commerce.model.gig.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementPaymentFailureRequest;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementPaymentResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementPaymentSuccessRequest;
import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelAgreement;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelAgreementPayment;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementPaymentFailedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelAgreementPaymentSucceededEvent;
import org.stylehub.backend.e_commerce.model.gig.publisher.ModelAgreementEventPublisher;
import org.stylehub.backend.e_commerce.model.gig.repository.ModelAgreementPaymentRepository;
import org.stylehub.backend.e_commerce.model.gig.repository.ModelAgreementRepository;
import org.stylehub.backend.e_commerce.model.profile.service.ModelProfileAccessService;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentMethod;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.platform.mail.events.ModelReviewRequestedEmailEvent;
import org.stylehub.backend.e_commerce.platform.mail.publisher.EmailEventPublisher;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModelAgreementPaymentService {

    private final ModelAgreementPaymentRepository modelAgreementPaymentRepository;
    private final ModelAgreementRepository modelAgreementRepository;
    private final ModelProfileAccessService modelProfileAccessService;
    private final CurrentUserProvider currentUserProvider;
    private final ModelAgreementEventPublisher modelAgreementEventPublisher;
    private final EmailEventPublisher emailEventPublisher;

    @Transactional
    public ModelAgreementPayment createPendingPayment(ModelAgreement agreement) {
        return this.modelAgreementPaymentRepository.findByAgreement_Id(agreement.getId())
                .orElseGet(() -> {
                    ModelAgreementPayment payment = new ModelAgreementPayment();
                    payment.setAgreement(agreement);
                    payment.setAmount(agreement.getAgreedPrice());
                    payment.setPaymentStatus(PaymentStatus.PENDING);
                    payment.setPaymentMethod(PaymentMethod.CARD);
                    payment.setProvider("FAKE");
                    return this.modelAgreementPaymentRepository.save(payment);
                });
    }

    public GigAgreementPaymentResponse findBrandPayment(UUID agreementId) {
        ModelAgreementPayment payment = findOrCreateBrandPayment(agreementId);
        return mapToResponse(payment, "Agreement payment loaded successfully");
    }

    public GigAgreementPaymentResponse findModelPayment(UUID agreementId) {
        ModelAgreementPayment payment = findOrCreateModelPayment(agreementId);
        return mapToResponse(payment, "Agreement payment loaded successfully");
    }

    @Transactional
    public GigAgreementPaymentResponse markPaymentSuccessful(UUID agreementId, GigAgreementPaymentSuccessRequest request) {
        ModelAgreementPayment payment = findOrCreateBrandPayment(agreementId);

        validatePaymentCanBeCompleted(payment.getAgreement(), payment);

        Instant paidAt = Instant.now();
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentMethod(resolvePaymentMethod(request == null ? null : request.paymentMethod()));
        payment.setProvider(resolveProvider(request == null ? null : request.provider()));
        payment.setProviderPaymentId(normalizeNullableText(request == null ? null : request.providerPaymentId()));
        payment.setTransactionReference(normalizeNullableText(request == null ? null : request.transactionReference()));
        payment.setFailureReason(null);
        payment.setPaidAt(paidAt);

        ModelAgreement agreement = payment.getAgreement();
        agreement.setPaymentStatus(PaymentStatus.PAID);
        agreement.setAgreementStatus(AgreementStatus.COMPLETED);
        agreement.setCompletedAt(paidAt);

        this.modelAgreementPaymentRepository.save(payment);
        this.modelAgreementRepository.save(agreement);

        this.modelAgreementEventPublisher.publishPaymentSucceeded(
                new ModelAgreementPaymentSucceededEvent(
                        agreement.getId(),
                        agreement.getAgreementNumber(),
                        payment.getId(),
                        agreement.getBrand().getUser().getId(),
                        agreement.getModelProfile().getUser().getId(),
                        paidAt,
                        agreement.getCompletedAt()
                )
        );

        this.emailEventPublisher.publishModelReviewRequested(
                new ModelReviewRequestedEmailEvent(
                        agreement.getId(),
                        agreement.getAgreementNumber(),
                        agreement.getBrand().getUser().getExternalUserId(),
                        resolveBrandName(agreement),
                        resolveBrandEmail(agreement),
                        agreement.getModelProfile().getModelName(),
                        agreement.getCompletedAt()
                )
        );

        return mapToResponse(payment, "Agreement payment completed successfully");
    }

    @Transactional
    public GigAgreementPaymentResponse markPaymentFailed(UUID agreementId, GigAgreementPaymentFailureRequest request) {
        if (request == null || request.failureReason() == null || request.failureReason().isBlank()) {
            throw new IllegalArgumentException("failureReason is required");
        }

        ModelAgreementPayment payment = findOrCreateBrandPayment(agreementId);

        validatePaymentCanFail(payment.getAgreement(), payment);

        Instant failedAt = Instant.now();
        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setPaymentMethod(resolvePaymentMethod(request.paymentMethod()));
        payment.setProvider(resolveProvider(request.provider()));
        payment.setProviderPaymentId(normalizeNullableText(request.providerPaymentId()));
        payment.setTransactionReference(normalizeNullableText(request.transactionReference()));
        payment.setFailureReason(request.failureReason().trim());
        payment.setPaidAt(null);

        ModelAgreement agreement = payment.getAgreement();
        agreement.setPaymentStatus(PaymentStatus.FAILED);
        agreement.setAgreementStatus(AgreementStatus.AWAITING_PAYMENT);
        agreement.setCompletedAt(null);

        this.modelAgreementPaymentRepository.save(payment);
        this.modelAgreementRepository.save(agreement);

        this.modelAgreementEventPublisher.publishPaymentFailed(
                new ModelAgreementPaymentFailedEvent(
                        agreement.getId(),
                        agreement.getAgreementNumber(),
                        payment.getId(),
                        agreement.getBrand().getUser().getId(),
                        agreement.getModelProfile().getUser().getId(),
                        payment.getFailureReason(),
                        failedAt
                )
        );

        return mapToResponse(payment, "Agreement payment failed");
    }

    private void validatePaymentCanBeCompleted(ModelAgreement agreement, ModelAgreementPayment payment) {
        if (agreement.getAgreementStatus() != AgreementStatus.AWAITING_PAYMENT) {
            throw new IllegalStateException("Agreement is not waiting for payment");
        }
        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Payment is already completed");
        }
    }

    private void validatePaymentCanFail(ModelAgreement agreement, ModelAgreementPayment payment) {
        if (agreement.getAgreementStatus() != AgreementStatus.AWAITING_PAYMENT) {
            throw new IllegalStateException("Agreement is not waiting for payment");
        }
        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Payment is already completed");
        }
    }

    private PaymentMethod resolvePaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethod == null ? PaymentMethod.CARD : paymentMethod;
    }

    private String resolveProvider(String provider) {
        String normalized = normalizeNullableText(provider);
        return normalized == null ? "FAKE" : normalized;
    }

    private String normalizeNullableText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String resolveBrandEmail(ModelAgreement agreement) {
        String brandEmail = normalizeNullableText(agreement.getBrand().getBrandEmail());
        if (brandEmail != null) {
            return brandEmail;
        }
        return agreement.getBrand().getUser().getEmail();
    }

    private String resolveBrandName(ModelAgreement agreement) {
        String brandName = normalizeNullableText(agreement.getBrand().getBrandName());
        return brandName == null ? "Brand partner" : brandName;
    }

    private ModelAgreementPayment findOrCreateBrandPayment(UUID agreementId) {
        return this.modelAgreementPaymentRepository.findByAgreementIdAndBrandExternalId(agreementId, currentUserProvider.externalId())
                .orElseGet(() -> {
                    ModelAgreement agreement = this.modelAgreementRepository
                            .findByIdAndBrandExternalId(agreementId, currentUserProvider.externalId())
                            .orElseThrow(() -> new IllegalArgumentException("Agreement not found"));
                    return createPendingPayment(agreement);
                });
    }

    private ModelAgreementPayment findOrCreateModelPayment(UUID agreementId) {
        this.modelProfileAccessService.requireCurrentModelProfile();

        return this.modelAgreementPaymentRepository.findByAgreementIdAndModelExternalId(agreementId, currentUserProvider.externalId())
                .orElseGet(() -> {
                    ModelAgreement agreement = this.modelAgreementRepository
                            .findByIdAndModelExternalId(agreementId, currentUserProvider.externalId())
                            .orElseThrow(() -> new IllegalArgumentException("Agreement not found"));
                    return createPendingPayment(agreement);
                });
    }

    private GigAgreementPaymentResponse mapToResponse(ModelAgreementPayment payment, String message) {
        ModelAgreement agreement = payment.getAgreement();
        return new GigAgreementPaymentResponse(
                payment.getId(),
                agreement.getId(),
                agreement.getAgreementNumber(),
                payment.getAmount(),
                payment.getPaymentStatus(),
                agreement.getAgreementStatus(),
                payment.getPaymentMethod(),
                payment.getProvider(),
                payment.getProviderPaymentId(),
                payment.getTransactionReference(),
                payment.getFailureReason(),
                payment.getPaidAt(),
                agreement.getCompletedAt(),
                message
        );
    }
}
