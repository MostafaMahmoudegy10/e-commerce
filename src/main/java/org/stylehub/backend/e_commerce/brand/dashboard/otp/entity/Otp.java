package org.stylehub.backend.e_commerce.brand.dashboard.otp.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.stylehub.backend.e_commerce.user.entity.User;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "otp_hash",columnDefinition = "varchar(255)", nullable = false)
    private String otpHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false,columnDefinition = "varchar(255)")
    private OtpChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose",nullable = false,columnDefinition = "varchar(255)")
    private OtpPurpose purpose;

    @Column(name = "recipient",nullable = false,columnDefinition = "varchar(255)")
    private String recipient;


    @Column(name = "expires_at",nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    @Column(name = "max_attempts")
    private Integer maxAttempts=5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isConsumed() {
        return this.consumedAt != null;
    }

    public boolean canAttemptVerification(){
        return !isExpired() && !isConsumed() && attemptCount < maxAttempts;
    }

    public void consume(){
        this.consumedAt = Instant.now();
    }

    public void recordFailedAttempt(){
        this.attemptCount++;
    }

}
