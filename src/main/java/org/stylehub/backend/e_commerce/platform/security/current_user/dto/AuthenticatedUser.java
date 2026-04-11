package org.stylehub.backend.e_commerce.platform.security.current_user.dto;

import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
        String externalId,
        String email,
        Set<String> roles,
        Boolean isProfileCompleted
        ) {

    public  UUID BrandId(){
       return roles.contains("BRAND_OWNER")?
               UUID.fromString(externalId):null;
    }
    public  UUID CustomerId(){
        return roles.contains("CUSTOMER")?
               UUID.fromString(externalId):null;
    }
}
