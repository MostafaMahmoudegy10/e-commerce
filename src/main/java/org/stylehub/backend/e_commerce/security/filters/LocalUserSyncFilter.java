package org.stylehub.backend.e_commerce.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.stylehub.backend.e_commerce.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.user.service.UserSyncService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LocalUserSyncFilter extends OncePerRequestFilter {

    private final UserSyncService userSyncService;

    private final CurrentUserProvider  currentUserProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // this filter come after we make user authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication !=null && authentication.getPrincipal() instanceof Jwt jwt){
            if(!currentUserProvider.isProfileCompleted()) {
                this.userSyncService.upsert(jwt.getSubject(), jwt.getClaimAsString("email"));
            }
        }

        filterChain.doFilter(request, response);
    }
}
