package org.stylehub.backend.e_commerce.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Role;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepository;

    @Transactional
    public User create(String externalId,String role,String email) {
        return sync(externalId, role, email);
    }

    @Transactional
    public User sync(String externalId, String role, String email) {
        User user = this.userRepository.findByExternalUserId(externalId)
                .orElseGet(User::new);

        user.setExternalUserId(externalId);
        user.setEmail(email);
        if(role.equals("CUSTOMER")){
            user.setRole(Role.CUSTOMER);
        }else if(role.equals("BRAND_OWNER")){
            user.setRole(Role.BRAND_OWNER);
        }else{
            throw new IllegalArgumentException("Invalid role");
        }

        user.setIsProfileCompleted(true);
        return this.userRepository.save(user);
    }

    @Transactional
    public void deleteByExternalId(String externalId) {
        this.userRepository.findByExternalUserId(externalId)
                .ifPresent(user -> this.userRepository.delete(user));
    }

}
