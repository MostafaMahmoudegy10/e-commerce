package org.stylehub.backend.e_commerce.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.cart.entity.CartStatus;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findCartByCartStatusAndCustomer_User_ExternalUserId(CartStatus cartStatus, String s);
}
