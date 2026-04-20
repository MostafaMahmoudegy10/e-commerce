package org.stylehub.backend.e_commerce.product.product_item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.product.product_item.size.Size;

import java.util.Optional;
import java.util.UUID;

public interface SizeRepo extends JpaRepository<Size, UUID> {

        Optional<Size> findByIdAndProductItem_IdAndProductItem_Product_Brand_User_ExternalUserId(
            UUID id,
            UUID productItemId,
            String externalUserId
    );
}
