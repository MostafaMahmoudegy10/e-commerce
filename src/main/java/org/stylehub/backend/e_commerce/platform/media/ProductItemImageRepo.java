package org.stylehub.backend.e_commerce.platform.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductItemImage;

import java.util.UUID;

public interface ProductItemImageRepo extends JpaRepository<ProductItemImage, UUID> {
}
