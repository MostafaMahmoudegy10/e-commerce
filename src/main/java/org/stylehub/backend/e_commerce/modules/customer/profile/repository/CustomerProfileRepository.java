package org.stylehub.backend.e_commerce.modules.customer.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.modules.customer.profile.entity.CustomerProfile;

import java.util.Optional;
import java.util.UUID;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {
    Optional<CustomerProfile> findByUser_ExternalUserId(String externalUserId);
    Optional<CustomerProfile> findByUsername(String username);
    boolean existsByUsernameAndUser_ExternalUserIdNot(String username, String externalUserId);
}
