package org.stylehub.backend.e_commerce.product.color.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.product.color.entity.ProductColor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductColorRepository extends JpaRepository<ProductColor, UUID> {

    @Query("""
        select pc from ProductColor pc
        join pc.product p
        join p.brand b
        join b.user u
        where
            pc.id=:productColorId and
            p.id=:productId and
            u.externalUserId=:brandUserExternalUserId
        """)
    Optional<ProductColor>findProductColorByIdAndProduct_IdAndProduct_Brand_User_ExternalUserId(
            UUID productColorId, UUID productId, String brandUserExternalUserId);

    @Query("""
        select pc from ProductColor pc
            join pc.product p
             where p.id=:productId and
                   pc.colorCode=:colorCode
        """)
    Optional<ProductColor> findProductColorByIdAndColorCode(UUID productId,String colorCode);

    List<ProductColor> findAllByProduct_IdAndProduct_Brand_User_ExternalUserId(UUID productId, String brandId);
}
