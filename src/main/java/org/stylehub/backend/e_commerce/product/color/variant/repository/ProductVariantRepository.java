package org.stylehub.backend.e_commerce.product.color.variant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.product.color.variant.entity.ProductVariant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    ProductVariant findProductVariantByProductColor_IdAndSize(UUID productColorId,String size);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);

    Optional<ProductVariant> findByIdAndProductColor_Id(UUID variantId, UUID productColorId);

     List<ProductVariant> findAllByProductColor_Id(UUID productColorId);

    void deleteAllByProductColor_Id(UUID productColorId);

    List<ProductVariant> findAllByProductColor_Product_Id(UUID productId);

    @Query("""
        select pv from  ProductVariant pv
        join pv.productColor pc
        join pc.product p
        join p.brand b
        join b.user u
        where pv.id=:productVariantId and
                u.externalUserId=:brandId
        """)
    Optional<ProductVariant> findProductVariantByIdAndBrandId(UUID productVariantId, String brandId);

}
