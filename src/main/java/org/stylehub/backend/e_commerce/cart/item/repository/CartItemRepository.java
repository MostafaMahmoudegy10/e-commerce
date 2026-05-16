package org.stylehub.backend.e_commerce.cart.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.cart.item.entity.CartItem;
import org.stylehub.backend.e_commerce.customer.dto.cart.CartItemViewResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    Optional<CartItem> findCartItemByCart_IdAndProductVariant_Id(UUID id, UUID id1);

    List<CartItem> findCartItemsByCart_Id(UUID cartId);

    @Query(value = """
        select new org.stylehub.backend.e_commerce.customer.dto.cart.CartItemViewResponse( 
                ca.username,
                b.brandName  
                ,p.productNameEn,
                p.productNameAr,
                p.thumbnail,
                ci.totalPrice,
                ci.quantity,
                ci.id,c.id,c.cartStatus,
                pv.id,pv.sku,
                pv.size,
                pc.colorCode,p.id)
        from CartItem ci
        join ci.cart c
        join ci.productVariant pv
        join pv.productColor pc
        join pc.product p
        join c.customer ca
        join c.brand b
        where c.id=:cartId
        """,countQuery = """
            select count(ci) from CartItem  ci
            join ci.cart c
            where c.id=:cartId
            """)
    Page<CartItemViewResponse> findCartViewResponseByCart_Id(UUID cartId, Pageable pageable);

    Optional<CartItem> findByIdAndCart_Id(UUID cartItemId, UUID cartId);

    boolean existsByCart_Id(UUID cartId);

    @Query("""
        select sum(ci.totalPrice) from CartItem  ci
        where ci.cart.id=:cartId 
                """)
    BigDecimal findTotalPriceByCartId(UUID cartId);

    void deleteAllByProductVariant_ProductColor_Product_Id(UUID productId);

    void deleteAllByProductVariant_ProductColor_Id(UUID productColorId);
}
