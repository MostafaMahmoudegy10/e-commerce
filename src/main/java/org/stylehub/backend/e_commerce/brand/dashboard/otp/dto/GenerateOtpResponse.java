package org.stylehub.backend.e_commerce.brand.dashboard.otp.dto;

import java.time.Instant;
import java.util.UUID;

public record GenerateOtpResponse(
        UUID otpId,
        String recipient,
        String purpose,
        Instant expiresAt,
        String otpCodeForTesting
) {
}
