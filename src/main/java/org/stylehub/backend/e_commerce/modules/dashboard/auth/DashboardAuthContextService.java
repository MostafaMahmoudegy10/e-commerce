package org.stylehub.backend.e_commerce.modules.dashboard.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.repository.BrandRepository;
import org.stylehub.backend.e_commerce.customer.profile.repository.CustomerProfileRepository;
import org.stylehub.backend.e_commerce.model.profile.repository.ModelProfileRepository;
import org.stylehub.backend.e_commerce.platform.security.current_user.dto.AuthenticatedUser;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Role;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class DashboardAuthContextService {

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ModelProfileRepository modelProfileRepository;

    public AuthenticatedUser buildByExternalId(String externalId) {
        User user = this.userRepository.findByExternalUserId(externalId)
                .orElseThrow(() -> new IllegalStateException("User not found with external id: " + externalId));
        return build(user);
    }

    public AuthenticatedUser build(User user) {
        boolean hasBrandProfile = this.brandRepository.existsByUser_ExternalUserId(user.getExternalUserId());
        boolean hasCustomerProfile = this.customerProfileRepository.existsByUser_ExternalUserId(user.getExternalUserId());
        boolean hasModelProfile = this.modelProfileRepository.existsByUser_Id(user.getId());
        boolean canAccessBrandDashboard = user.getRole() == Role.BRAND_OWNER && hasBrandProfile;
        boolean canAccessModelDashboard = user.getRole() == Role.CUSTOMER && hasModelProfile;

        return new AuthenticatedUser(
                user.getId(),
                user.getExternalUserId(),
                user.getEmail(),
                user.getRole().name(),
                Set.of(user.getRole().name()),
                Boolean.TRUE.equals(user.getIsProfileCompleted()),
                hasBrandProfile,
                hasCustomerProfile,
                hasModelProfile,
                canAccessBrandDashboard,
                canAccessModelDashboard,
                resolveDefaultDashboard(user.getRole(), hasBrandProfile, hasModelProfile)
        );
    }

    private String resolveDefaultDashboard(Role role, boolean hasBrandProfile, boolean hasModelProfile) {
        if (role == Role.BRAND_OWNER) {
            return "BRAND";
        }
        if (role == Role.CUSTOMER && hasModelProfile) {
            return "MODEL";
        }
        return "CUSTOMER";
    }
}
