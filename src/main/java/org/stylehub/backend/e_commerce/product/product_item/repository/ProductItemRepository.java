package org.stylehub.backend.e_commerce.product.product_item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;

import java.util.Optional;
import java.util.UUID;

public interface ProductItemRepository extends JpaRepository<ProductItem, UUID> {
    Optional<ProductItem> findByIdAndProduct_IdAndProduct_Brand_User_ExternalUserId(UUID productItemId,UUID productId, String brandId);

    boolean existsByIdAndColorAndProduct_IdAndProduct_Brand_User_ExternalUserId(UUID productItemId, String color, UUID productId ,String brandId);
}
