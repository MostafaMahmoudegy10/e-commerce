package org.stylehub.backend.e_commerce.platform.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record AppJwtProperties(
        String issuer,
        String audience,
        String secret

) {
}
