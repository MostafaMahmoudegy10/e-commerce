package org.stylehub.backend.e_commerce.order.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.order.address.entity.ShippingAddress;

import java.util.UUID;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, UUID> { }
