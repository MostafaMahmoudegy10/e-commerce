package org.stylehub.backend.e_commerce.platform.security.current_user.dto;

import java.util.Set;

public record AuthenticatedUser(
        String externalId,
        String email,
        Set<String> roles,
        Boolean isProfileCompleted
) {
}
