package org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.dto;

import org.stylehub.backend.e_commerce.platform.security.current_user.dto.AuthenticatedUser;

public record VerifyOtpResponse(
        boolean verified,
        String message,
        int remainingAttempts,
        String accessToken,
        String refreshToken,
        AuthenticatedUser user
) {
    public static VerifyOtpResponse failed(String message, int remainingAttempts) {
        return new VerifyOtpResponse(false,message,remainingAttempts,null,null,null);
    }
}
