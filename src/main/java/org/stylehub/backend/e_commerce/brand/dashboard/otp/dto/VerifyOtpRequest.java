package org.stylehub.backend.e_commerce.brand.dashboard.otp.dto;

import org.stylehub.backend.e_commerce.brand.dashboard.otp.entity.OtpPurpose;

public record VerifyOtpRequest(
        String recipient,
        OtpPurpose purpose,
        String otpCode
) {
}
