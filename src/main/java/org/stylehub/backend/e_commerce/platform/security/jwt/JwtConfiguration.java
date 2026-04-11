package org.stylehub.backend.e_commerce.platform.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.util.Assert;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@RequiredArgsConstructor
public class JwtConfiguration {

    private final AppJwtProperties appJwtProperties;

    @Bean
    public JwtDecoder jwtDecoder(){
        final String SECRET_NOT_PROVIDED="JWT secret must be provided via JWT_SHARED_SECRET or JWT_TOKEN environment variable.";
        // first we make sure that token include a secret
        Assert.hasText(appJwtProperties.secret(),SECRET_NOT_PROVIDED);

        //Convert this secret into a key can decoder understand
        SecretKeySpec secretKeySpec=new SecretKeySpec(appJwtProperties.secret().getBytes(),
                "HmacSHA256");

        // build the key with this secret
        NimbusJwtDecoder decoder= NimbusJwtDecoder.withSecretKey(secretKeySpec).build();

//        OAuth2TokenValidator<Jwt> withIssuer= JwtValidators.createDefaultWithIssuer(appJwtProperties.issuer());
//
//        OAuth2TokenValidator<Jwt> audienceValidator=new AudienceValidator(appJwtProperties.audience());

        // collect all validators
        decoder.setJwtValidator(JwtValidators.createDefault());

        return decoder;
    }
    @Bean
    public JwtEncoder jwtEncoder(){
        SecretKeySpec secretKeySpec= new SecretKeySpec(appJwtProperties.secret().getBytes(),"HmacSHA256");
        return NimbusJwtEncoder.withSecretKey(secretKeySpec).build();
    }
}
