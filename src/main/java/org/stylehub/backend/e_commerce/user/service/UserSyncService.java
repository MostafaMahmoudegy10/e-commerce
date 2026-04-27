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
    public User create(String externalId,String role) {

        if (this.userRepository.existsByExternalUserId(externalId)) {
            throw new IllegalArgumentException("User already exists");
        }

        User newUser = new User();
        newUser.setExternalUserId(externalId);
        newUser.setEmail(externalId);
        if(role.equals("CUSTOMER")){
            newUser.setRole(Role.CUSTOMER);
        }else if(role.equals("BRAND_OWNER")){
            newUser.setRole(Role.BRAND_OWNER);
        }else{
            throw new IllegalArgumentException("Invalid role");
        }

        newUser.setIsProfileCompleted(true);
        return this.userRepository.save(newUser);
    }

}
