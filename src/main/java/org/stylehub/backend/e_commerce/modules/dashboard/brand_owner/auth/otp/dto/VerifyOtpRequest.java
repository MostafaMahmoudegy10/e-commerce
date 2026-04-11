package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.auth.otp.dto;

import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.auth.otp.entity.OtpPurpose;

public record VerifyOtpRequest(
        String recipient,
        OtpPurpose purpose,
        String otpCode
) {
}
