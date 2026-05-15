package org.stylehub.backend.e_commerce.modules.dashboard.auth.otp.otp.dto;

import org.stylehub.backend.e_commerce.platform.security.current_user.dto.AuthenticatedUser;

public record RefreshTokenResponse(String accessToken, String refreshToken, AuthenticatedUser user) {
}
