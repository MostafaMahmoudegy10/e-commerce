package org.stylehub.backend.e_commerce.modules.customer.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.modules.customer.address.entity.CustomerAddress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, UUID> {

    List<CustomerAddress> findAllByUser_ExternalUserIdOrderByCreatedAtDesc(String externalUserId);

    Optional<CustomerAddress> findByIdAndUser_ExternalUserId(UUID addressId, String externalUserId);

    Optional<CustomerAddress> findByUser_ExternalUserIdAndIsDefaultTrue(String externalUserId);

    List<CustomerAddress> findAllByUser_ExternalUserId(String externalUserId);
}
