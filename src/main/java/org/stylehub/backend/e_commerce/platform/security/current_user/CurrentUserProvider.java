package org.stylehub.backend.e_commerce.platform.security.current_user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.platform.security.current_user.dto.AuthenticatedUser;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public AuthenticatedUser getCurrentUser() {
        String externalId = externalId();

        User user = userRepository.findByExternalUserId(externalId)
                .orElseThrow(() -> new IllegalStateException("User not found with external id: " + externalId));

        return new AuthenticatedUser(user.getId(), externalId, getEmail(), getRoles(), isProfileCompleted());
    }

    public Set<String> getRoles() {
        Authentication authentication = getAuthentication();
        return authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toSet());
    }

    public String getEmail() {
        return getJwt().getClaimAsString("email");
    }

    public String externalId() {
        return getJwt().getSubject();
    }

    public Boolean isProfileCompleted() {
        return getJwt().getClaimAsBoolean("isProfileComplete");
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
