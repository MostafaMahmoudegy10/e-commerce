package org.stylehub.backend.e_commerce.cart.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    Optional<CartItem> findCartItemByCart_IdAndProductVariant_Id(UUID id, UUID id1);
}
