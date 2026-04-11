package org.stylehub.backend.e_commerce.brand.dashboard.otp.dto;

import org.stylehub.backend.e_commerce.brand.dashboard.otp.entity.OtpPurpose;

public record VerifyOtpResponse(
        boolean verified,
        String message,
        int remainingAttempts
) {
}
