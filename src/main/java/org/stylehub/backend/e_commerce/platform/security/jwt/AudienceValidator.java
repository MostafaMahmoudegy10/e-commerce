package org.stylehub.backend.e_commerce.platform.security.jwt;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@AllArgsConstructor
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_AUDIENCE=
            new OAuth2Error(
                    "invalid_token",
                    "The required audience is missing.",
                    null
            );

    private String requiredAudience;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
       List<String> audiences= token.getAudience();

       if(!audiences.isEmpty()&&audiences.contains(requiredAudience)){
           return OAuth2TokenValidatorResult.success();
       }
       return OAuth2TokenValidatorResult.failure(INVALID_AUDIENCE);
    }
}
