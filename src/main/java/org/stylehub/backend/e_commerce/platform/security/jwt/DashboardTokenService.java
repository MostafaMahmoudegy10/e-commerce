package org.stylehub.backend.e_commerce.platform.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.repository.BrandRepository;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardTokenService {

    private static final long ACCESS_TOKEN_SECONDS= 15*60;

    private static final long REFRESH_TOKEN_SECONDS= 7 * 24 * 60 * 60;

    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    private final AppJwtProperties  appJwtProperties;

    private final BrandRepository brandRepository;

    public TokenPair generateTokenPair(User user){
        String role = (brandRepository.existsByUser_Id(user.getId()) ? "BRAND_OWNER":"CUSTOMER");

        return new TokenPair(
                createToken(user,role,"access",ACCESS_TOKEN_SECONDS),
                createToken(user,role,"refresh",REFRESH_TOKEN_SECONDS)
        );

    }
    public TokenPair refresh(String refreshToken){
        Jwt jwt =jwtDecoder.decode(refreshToken);

        String tokenType=jwt.getClaimAsString("token_type");
        if (!tokenType.equals("refresh")) {
            throw new IllegalArgumentException("Provided token is not a refresh token");
        }
        // get the claims and return token pair
        String subject =jwt.getClaimAsString("subject");
        String email =jwt.getClaimAsString("email");
        String role =jwt.getClaimAsString("role");

        return new TokenPair(
                createToken(subject,email,role,"access",ACCESS_TOKEN_SECONDS),
                createToken(subject,email,role,"refresh",REFRESH_TOKEN_SECONDS)
        );
    }

    private String createToken(User user, String role, String tokenType, long seconds) {
        return createToken(user.getId().toString(), user.getEmail(), role, tokenType, seconds);
    }

    private String createToken(String subject, String email,
                               String role, String tokenType, long seconds) {
        Instant instant=Instant.now();
        JwtClaimsSet jwtClaimsSet= JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(instant)
                .expiresAt(instant.plusSeconds(seconds))
                .issuer(appJwtProperties.issuer())
                .audience(List.of(appJwtProperties.audience()))
                .claim("email",email)
                .claim("role",role)
                .claim("token_type",tokenType)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet))
                .getTokenValue();
    }


    public record TokenPair(String accessToken, String refreshToken) {}
}
