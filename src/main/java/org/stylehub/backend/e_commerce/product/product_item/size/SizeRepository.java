package org.stylehub.backend.e_commerce.product.product_item.size;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SizeRepository extends JpaRepository<Size, UUID> {

    void deleteByProductItem_Id(UUID productItemId);
}
