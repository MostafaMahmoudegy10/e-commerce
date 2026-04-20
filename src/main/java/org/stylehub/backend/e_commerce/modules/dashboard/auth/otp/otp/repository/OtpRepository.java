package org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.entity.Otp;
import org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.entity.OtpPurpose;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {
    Optional<Otp> findTopByRecipientAndPurposeOrderByCreatedAtDesc(String recipient, OtpPurpose purpose);}
