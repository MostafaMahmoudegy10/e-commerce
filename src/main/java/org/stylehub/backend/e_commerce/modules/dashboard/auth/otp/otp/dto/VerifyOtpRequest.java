package org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.dto;

import org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.entity.OtpPurpose;

public record VerifyOtpRequest(
        String recipient,
        OtpPurpose purpose,
        String otpCode
) {
}
