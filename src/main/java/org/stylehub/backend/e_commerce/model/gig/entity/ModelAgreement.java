package org.stylehub.backend.e_commerce.model.gig.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfile;
import org.stylehub.backend.e_commerce.model.profile.enums.AvailableFor;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "model_agreement")
public class ModelAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "agreement_number", nullable = false, unique = true, length = 50)
    private String agreementNumber;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "model_profile_id", nullable = false)
    private ModelProfile modelProfile;

    @OneToOne
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private ModelGigRequest request;

    @Enumerated(EnumType.STRING)
    @Column(name = "available_for", nullable = false, length = 100)
    private AvailableFor availableFor;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(name = "agreed_price", nullable = false)
    private BigDecimal agreedPrice;

    private Instant deadline;

    @Column(length = 500)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "agreement_status", nullable = false, length = 50)
    private AgreementStatus agreementStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 50)
    private PaymentStatus paymentStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @PrePersist
    void onCreate() {
        if (this.agreementStatus == null) {
            this.agreementStatus = AgreementStatus.IN_PROGRESS;
        }
        if (this.paymentStatus == null) {
            this.paymentStatus = PaymentStatus.PENDING;
        }
        if (this.agreementNumber == null || this.agreementNumber.isBlank()) {
            this.agreementNumber = "AG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
