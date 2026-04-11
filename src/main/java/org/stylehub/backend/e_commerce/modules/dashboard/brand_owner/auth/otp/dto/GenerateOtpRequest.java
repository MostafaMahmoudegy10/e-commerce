package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.auth.otp.dto;

import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.auth.otp.entity.OtpChannel;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.auth.otp.entity.OtpPurpose;

public record GenerateOtpRequest(
        String email,
        String recipient,
        OtpPurpose purpose,
        OtpChannel channel,
        Integer expiryMinutes
) {
}
