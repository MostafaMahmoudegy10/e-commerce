package org.stylehub.backend.e_commerce.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Role;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepository;

    @Transactional
    public User upsert(CurrentUserProvider currentUserProvider) {
        return this.userRepository
                .findByExternalUserId(currentUserProvider.externalId())
                .map(user -> {
                    if (currentUserProvider.getEmail() != null &&
                            !currentUserProvider.getEmail().equals(user.getEmail())) {
                        user.setEmail(currentUserProvider.getEmail());
                    }
                    return user;
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setExternalUserId(currentUserProvider.externalId());
                    newUser.setEmail(currentUserProvider.getEmail());
                    newUser.setRole(Role.valueOf(
                            currentUserProvider.getRoles().stream().findFirst().orElse("CUSTOMER")
                    ));
                    newUser.setIsProfileCompleted(true);
                    return this.userRepository.save(newUser);
                });
    }

}
