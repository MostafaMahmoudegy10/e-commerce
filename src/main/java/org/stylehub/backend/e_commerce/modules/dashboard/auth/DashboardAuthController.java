package org.stylehub.backend.e_commerce.modules.dashboard.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.platform.security.current_user.dto.AuthenticatedUser;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class DashboardAuthController {

    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/me")
    public AuthenticatedUser me() {
        return this.currentUserProvider.getCurrentUser();
    }
}
