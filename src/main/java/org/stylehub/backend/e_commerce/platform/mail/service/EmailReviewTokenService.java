package org.stylehub.backend.e_commerce.platform.mail.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class EmailReviewTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Value("${app.email-review.token-secret:stylehub-email-review-secret-change-me}")
    private String tokenSecret;

    @Value("${app.email-review.token-validity-days:30}")
    private long tokenValidityDays;

    public String createModelReviewToken(UUID agreementId, String brandExternalId) {
        return createToken("MODEL", brandExternalId, agreementId, null);
    }

    public String createProductReviewToken(UUID orderId, UUID productId, String customerExternalId) {
        return createToken("PRODUCT", customerExternalId, orderId, productId);
    }

    public ModelReviewClaim verifyModelReviewToken(String token) {
        TokenPayload payload = verifyToken(token, "MODEL");
        return new ModelReviewClaim(payload.actorExternalId(), payload.referenceId());
    }

    public ProductReviewClaim verifyProductReviewToken(String token) {
        TokenPayload payload = verifyToken(token, "PRODUCT");
        if (payload.secondaryId() == null) {
            throw new IllegalArgumentException("Invalid product review token");
        }
        return new ProductReviewClaim(payload.actorExternalId(), payload.referenceId(), payload.secondaryId());
    }

    private String createToken(String type, String actorExternalId, UUID referenceId, UUID secondaryId) {
        long expiresAt = Instant.now().plusSeconds(this.tokenValidityDays * 24 * 60 * 60).getEpochSecond();
        String rawPayload = String.join("|",
                type,
                actorExternalId,
                referenceId.toString(),
                secondaryId == null ? "-" : secondaryId.toString(),
                Long.toString(expiresAt)
        );

        String encodedPayload = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(rawPayload.getBytes(StandardCharsets.UTF_8));
        String signature = sign(encodedPayload);
        return encodedPayload + "." + signature;
    }

    private TokenPayload verifyToken(String token, String expectedType) {
        if (token == null || token.isBlank() || !token.contains(".")) {
            throw new IllegalArgumentException("Invalid review token");
        }

        String[] parts = token.split("\\.", 2);
        String encodedPayload = parts[0];
        String providedSignature = parts[1];
        String expectedSignature = sign(encodedPayload);

        if (!MessageDigest.isEqual(
                providedSignature.getBytes(StandardCharsets.UTF_8),
                expectedSignature.getBytes(StandardCharsets.UTF_8)
        )) {
            throw new IllegalArgumentException("Invalid review token signature");
        }

        String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
        String[] segments = payload.split("\\|", -1);
        if (segments.length != 5) {
            throw new IllegalArgumentException("Invalid review token payload");
        }

        String type = segments[0];
        if (!expectedType.equals(type)) {
            throw new IllegalArgumentException("Invalid review token type");
        }

        long expiresAt = Long.parseLong(segments[4]);
        if (Instant.now().getEpochSecond() > expiresAt) {
            throw new IllegalArgumentException("Review token has expired");
        }

        return new TokenPayload(
                type,
                segments[1],
                UUID.fromString(segments[2]),
                "-".equals(segments[3]) ? null : UUID.fromString(segments[3]),
                expiresAt
        );
    }

    private String sign(String encodedPayload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(this.tokenSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] signature = mac.doFinal(encodedPayload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to sign review token", exception);
        }
    }

    private record TokenPayload(
            String type,
            String actorExternalId,
            UUID referenceId,
            UUID secondaryId,
            long expiresAt
    ) {
    }

    public record ModelReviewClaim(
            String brandExternalId,
            UUID agreementId
    ) {
    }

    public record ProductReviewClaim(
            String customerExternalId,
            UUID orderId,
            UUID productId
    ) {
    }
}
