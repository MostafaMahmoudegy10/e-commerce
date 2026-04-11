package org.stylehub.backend.e_commerce.platform.security.current_user;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated user in the current security context.");
        }
        //find user thet added in the db
        String externalId= jwt.getSubject();
        String email =jwt.getClaimAsString("email");
        Set<String> roles=authentication.getAuthorities().
                stream().
                map(authority->
                    authority.getAuthority().replace("ROLE_","")
                ).collect(Collectors.toSet());

        Boolean isProfileCompleted=jwt.getClaimAsBoolean("isProfileComplete");
       User user= userRepository.findByExternalUserId(externalId).orElseThrow(()->new IllegalStateException("User not found with external id: "+externalId));
        return new AuthenticatedUser(user.getId(),externalId,email,roles,isProfileCompleted);
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
    public  Boolean isProfileCompleted() {
        return this.getCurrentUser().isProfileCompleted();
    }
    public UUID getUserId() {
        return this.getCurrentUser().userId();
    }

}
