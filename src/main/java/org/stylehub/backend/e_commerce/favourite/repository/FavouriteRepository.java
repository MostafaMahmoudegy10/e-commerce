package org.stylehub.backend.e_commerce.favourite.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.favourite.dto.WishlistView;
import org.stylehub.backend.e_commerce.favourite.entity.Favourite;

import java.util.Optional;
import java.util.UUID;

public interface FavouriteRepository extends JpaRepository<Favourite, UUID> {


    Optional<Favourite> findFavouriteByBrand_IdAndCustomer_IdAndProduct_Id(UUID brandId, UUID customerId, UUID productId);

    @Query(value = """
        select new org.stylehub.backend.e_commerce.favourite.dto.WishlistView(
           ca.username,b.brandName,p.id,p.productNameEn,p.productNameAr
           ,p.thumbnail,p.productDescriptionEn,p.productDescriptionAr
           ,c.categoryNameEn,c.categoryNameAr
           )
        from Favourite f
        join f.product p
        join p.category c
        join f.customer ca
        join f.brand b
        where f.customer.id=:customerId 
               and f.brand.id=:brandId
       order by f.createAt , f.updateAt
       """,countQuery = """
            select count (f)from  Favourite  f
                where f.customer.id=:customerId 
               and f.brand.id=:brandId 
            """)
    Page<WishlistView> viewWishlistForCustomer_IdInBrand_Id(UUID customerId, UUID brandId, Pageable pageable);

    void deleteByBrand_IdAndProduct_IdAndCustomer_Id(UUID id, UUID id1, UUID id2);

    void deleteByProduct_Id(UUID productId);
}
