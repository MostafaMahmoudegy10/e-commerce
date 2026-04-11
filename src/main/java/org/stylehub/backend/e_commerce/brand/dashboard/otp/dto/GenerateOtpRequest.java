package org.stylehub.backend.e_commerce.brand.dashboard.otp.dto;

import org.stylehub.backend.e_commerce.brand.dashboard.otp.entity.OtpChannel;
import org.stylehub.backend.e_commerce.brand.dashboard.otp.entity.OtpPurpose;

public record GenerateOtpRequest(
        String email,
        String recipient,
        OtpPurpose purpose,
        OtpChannel channel,
        Integer expiryMinutes
) {
}
