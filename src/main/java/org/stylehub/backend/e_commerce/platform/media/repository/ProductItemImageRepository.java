package org.stylehub.backend.e_commerce.platform.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductItemImage;

import java.util.List;
import java.util.UUID;

public interface ProductItemImageRepository extends JpaRepository<ProductItemImage, UUID> {

    List<ProductItemImage> findAllByProductItem_Id(UUID productItemId);

    void deleteByProductItem_Id(UUID productItemId);
}
