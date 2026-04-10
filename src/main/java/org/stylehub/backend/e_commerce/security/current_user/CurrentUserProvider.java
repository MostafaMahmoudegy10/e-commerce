package org.stylehub.backend.e_commerce.security.current_user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.security.current_user.dto.AuthenticatedUser;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CurrentUserProvider {
    public AuthenticatedUser getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated user in the current security context.");
        }

        String externalId= jwt.getSubject();
        String email =jwt.getClaimAsString("email");
        Set<String> roles=authentication.getAuthorities().
                stream().
                map(authority->
                    authority.getAuthority().replace("ROLE_","")
                ).collect(Collectors.toSet());

        return new AuthenticatedUser(externalId,email,roles);
    }

    public  Set<String> getRoles() {
        return this.getCurrentUser().roles();
    }

    public String getEmail(){
        return this.getCurrentUser().email();
    }

    public String externalId(){
        return this.getCurrentUser().externalId();
    }
}
