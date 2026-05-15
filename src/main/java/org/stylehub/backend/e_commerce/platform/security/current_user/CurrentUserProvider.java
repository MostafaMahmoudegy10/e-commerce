package org.stylehub.backend.e_commerce.platform.security.current_user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.modules.dashboard.auth.DashboardAuthContextService;
import org.stylehub.backend.e_commerce.platform.security.current_user.dto.AuthenticatedUser;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final DashboardAuthContextService dashboardAuthContextService;


    public AuthenticatedUser getCurrentUser() {
        return this.dashboardAuthContextService.buildByExternalId(externalId());
    }

    public String getEmail() {
        return getCurrentUser().email();
    }

    public String externalId() {
        return getJwt().getSubject();
    }

    public Boolean isProfileCompleted() {
        return getCurrentUser().isProfileCompleted();
    }

    public UUID getUserId() {
        return this.getCurrentUser().userId();
    }

    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            throw new IllegalStateException("No authenticated user in the current security context.");
        }
        return authentication;
    }

    private Jwt getJwt() {
        return (Jwt) getAuthentication().getPrincipal();
    }
}
