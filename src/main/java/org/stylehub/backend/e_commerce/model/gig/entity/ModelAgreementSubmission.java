package org.stylehub.backend.e_commerce.model.gig.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "model_agreement_submission")
public class ModelAgreementSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "agreement_id", nullable = false)
    private ModelAgreement agreement;

    @Column(length = 3000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false, length = 50)
    private SubmissionReviewStatus reviewStatus;

    @Column(name = "review_feedback", length = 3000)
    private String reviewFeedback;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt asc, id asc")
    private List<ModelAgreementSubmissionAsset> assets = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (this.reviewStatus == null) {
            this.reviewStatus = SubmissionReviewStatus.PENDING;
        }
    }

    public void addAsset(ModelAgreementSubmissionAsset asset) {
        asset.setSubmission(this);
        this.assets.add(asset);
    }
}
