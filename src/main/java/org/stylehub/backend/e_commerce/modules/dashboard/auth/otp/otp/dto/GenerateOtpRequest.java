package org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.dto;

import org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.entity.OtpChannel;
import org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.entity.OtpPurpose;

public record GenerateOtpRequest(
        String email,
        String recipient,
        OtpPurpose purpose,
        OtpChannel channel,
        Integer expiryMinutes
) {
}
