package org.stylehub.backend.e_commerce.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.cart.entity.Cart;
import org.stylehub.backend.e_commerce.cart.entity.CartStatus;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    @Query("""
    select c
    from Cart c
    join c.customer cp
    join cp.user u
    where u.externalUserId = :externalUserId
      and c.cartStatus = :status
""")
    Optional<Cart> findActiveCartByCustomerExternalUserId(
            String externalUserId,
            CartStatus status );
}
