package org.stylehub.backend.e_commerce.brand.dashboard.otp.repoistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.entity.Otp;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.entity.OtpPurpose;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {
    Optional<Otp> findTopByRecipientAndPurposeOrderByCreatedAtDesc(String recipient, OtpPurpose purpose);}
