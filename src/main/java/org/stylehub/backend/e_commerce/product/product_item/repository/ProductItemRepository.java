package org.stylehub.backend.e_commerce.product.product_item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;

import java.util.UUID;

public interface ProductItemRepository extends JpaRepository<ProductItem, UUID> {
}
