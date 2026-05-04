package org.stylehub.backend.e_commerce.cart.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

//    Optional<CartItem> findCartItemByProductItem_IdAndCart_Id(UUID productId, UUID cartId);
}
