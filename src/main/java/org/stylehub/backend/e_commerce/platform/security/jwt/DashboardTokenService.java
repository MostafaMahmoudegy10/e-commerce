package org.stylehub.backend.e_commerce.platform.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.dashboard.auth.DashboardAuthContextService;
import org.stylehub.backend.e_commerce.platform.security.current_user.dto.AuthenticatedUser;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardTokenService {

    private static final long ACCESS_TOKEN_SECONDS= 15*60;

    private static final long REFRESH_TOKEN_SECONDS= 7 * 24 * 60 * 60;

    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    private final AppJwtProperties  appJwtProperties;

    private final UserRepository userRepository;
    private final DashboardAuthContextService dashboardAuthContextService;

    public TokenPair generateTokenPair(User user){
        return new TokenPair(
                createToken(user,"access",ACCESS_TOKEN_SECONDS),
                createToken(user,"refresh",REFRESH_TOKEN_SECONDS)
        );

    }
    public TokenPair refresh(String refreshToken){
        Jwt jwt =jwtDecoder.decode(refreshToken);

        String tokenType=jwt.getClaimAsString("token_type");
        if (!"refresh".equals(tokenType)) {
            throw new IllegalArgumentException("Provided token is not a refresh token");
        }

        String subject = jwt.getSubject();
        User user = this.userRepository.findByExternalUserId(subject)
                .orElseThrow(() -> new IllegalArgumentException("User not found for refresh token"));
        return generateTokenPair(user);
    }

    public String extractSubject(String token) {
        return this.jwtDecoder.decode(token).getSubject();
    }

    private String createToken(User user, String tokenType, long seconds) {
        Instant instant=Instant.now();
        AuthenticatedUser authenticatedUser = this.dashboardAuthContextService.build(user);
        JwtClaimsSet jwtClaimsSet= JwtClaimsSet.builder()
                .subject(user.getExternalUserId())
                .issuedAt(instant)
                .expiresAt(instant.plusSeconds(seconds))
                .issuer(appJwtProperties.issuer())
                .audience(List.of(appJwtProperties.audience()))
                .claim("email",user.getEmail())
                .claim("role",authenticatedUser.role())
                .claim("roles", List.copyOf(authenticatedUser.roles()))
                .claim("token_type",tokenType)
                .claim("isProfileCompleted", authenticatedUser.isProfileCompleted())
                .claim("hasBrandProfile", authenticatedUser.hasBrandProfile())
                .claim("hasCustomerProfile", authenticatedUser.hasCustomerProfile())
                .claim("hasModelProfile", authenticatedUser.hasModelProfile())
                .claim("canAccessBrandDashboard", authenticatedUser.canAccessBrandDashboard())
                .claim("canAccessModelDashboard", authenticatedUser.canAccessModelDashboard())
                .claim("defaultDashboard", authenticatedUser.defaultDashboard())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet))
                .getTokenValue();
    }


    public record TokenPair(String accessToken, String refreshToken) {}
}
