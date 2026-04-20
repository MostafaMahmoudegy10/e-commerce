package org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.dto;

public record VerifyOtpResponse(
        boolean verified,
        String message,
        int remainingAttempts,
        String accessToken,
        String refreshToken
) {
    public static VerifyOtpResponse failed(String message, int remainingAttempts) {
        return new VerifyOtpResponse(false,message,remainingAttempts,null,null);
    }
}
