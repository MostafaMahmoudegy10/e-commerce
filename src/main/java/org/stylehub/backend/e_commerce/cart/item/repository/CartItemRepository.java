package org.stylehub.backend.e_commerce.cart.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    Optional<CartItem> findByCart_IdAndProductItem_IdAndSizeName(UUID cartId, UUID productItemId, String sizeName);

    List<CartItem> findAllByCart_Id(UUID cartId);

    @Query("""
            select ci
            from CartItem ci
            join ci.productItem pi
            join pi.product p
            join p.brand b
            where ci.cart.id = :cartId
              and b.id = :brandId
            """)
    List<CartItem> findAllByCart_IdAndBrand_Id(UUID cartId, UUID brandId);
}
