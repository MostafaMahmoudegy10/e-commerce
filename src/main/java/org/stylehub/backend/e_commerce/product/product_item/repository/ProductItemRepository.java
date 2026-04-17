package org.stylehub.backend.e_commerce.product.product_item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductItemRepository extends JpaRepository<ProductItem, UUID> {

    @Query("""
                select (count(pi) > 0)
                from ProductItem pi
                join pi.product p
                join p.brand b
                join b.user u
                where p.id = :productId
                  and pi.color = :color
                  and u.externalUserId = :brandId
            """)
    boolean existsByProduct_IdAndColorAndProduct_Brand_User_ExternalUserId(UUID productId, String color, String brandId);

    @Query("""
                select (count(pi) > 0)
                from ProductItem pi
                join pi.product p
                join p.brand b
                join b.user u
                where p.id = :productId
                  and pi.color = :color
                  and u.externalUserId = :brandId
                  and pi.id <> :productItemId
            """)
    boolean existsByProduct_IdAndColorAndProduct_Brand_User_ExternalUserIdAndIdNot(
            UUID productId,
            String color,
            String brandId,
            UUID productItemId
    );

    @Query("""
                select pi
                from ProductItem pi
                join pi.product p
                join p.brand b
                join b.user u
                where pi.id = :productItemId
                  and u.externalUserId = :brandId
            """)
    Optional<ProductItem> findProductItemByIdAndProduct_Brand_User_ExternalUserId(UUID productItemId, String brandId);

    List<ProductItem> findAllByProduct_Id(UUID productId);
}
