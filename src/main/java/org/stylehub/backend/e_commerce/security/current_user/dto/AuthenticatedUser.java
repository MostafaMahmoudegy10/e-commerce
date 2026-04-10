package org.stylehub.backend.e_commerce.security.current_user.dto;

import java.util.Set;

public record AuthenticatedUser(
        String externalId,
        String email,
        Set<String> roles
) {
}
