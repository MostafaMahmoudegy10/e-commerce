package org.stylehub.backend.e_commerce.platform.security.current_user.dto;

import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String externalId,
        String email,
        String role,
        Set<String> roles,
        Boolean isProfileCompleted,
        Boolean hasBrandProfile,
        Boolean hasCustomerProfile,
        Boolean hasModelProfile,
        Boolean canAccessBrandDashboard,
        Boolean canAccessModelDashboard,
        String defaultDashboard
        ) {


}
