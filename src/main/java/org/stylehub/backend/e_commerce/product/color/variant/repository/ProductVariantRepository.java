package org.stylehub.backend.e_commerce.product.color.variant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;

import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    ProductVariant findProductVariantByProductColor_IdAndSize(UUID productColorId,String size);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);

    Optional<ProductVariant> findByIdAndProductColor_Id(UUID variantId, UUID productColorId);

    java.util.List<ProductVariant> findAllByProductColor_Id(UUID productColorId);

    void deleteAllByProductColor_Id(UUID productColorId);

}
