package org.stylehub.backend.e_commerce.product.product_item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.product.product_item.size.Size;

import java.util.UUID;

public interface SizeRepo extends JpaRepository<Size, UUID> {
}
