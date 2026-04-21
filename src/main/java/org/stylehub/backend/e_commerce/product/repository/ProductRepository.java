package org.stylehub.backend.e_commerce.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.CustomerShowProductDetailsDto;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("""
                select (count(p) > 0)
                    from Product p
                    join p.brand b
                    join b.user u
                    where p.productNameEn = :productName
                      and u.externalUserId = :generalBrandId
                                                        """)
   boolean existsProductByBrand_User_ExternalUserId(String productName,String generalBrandId);

    @Query("""
                select p
                from Product p
                join p.brand b
                join b.user u
                where p.id = :productId
                  and u.externalUserId = :generalBrandId
            """)
    Optional<Product> findProductByIdAndBrand_User_ExternalUserId(UUID productId, String generalBrandId);

    @Query("""
                select (count(p) > 0)
                from Product p
                join p.brand b
                join b.user u
                where p.productNameEn = :productName
                  and u.externalUserId = :generalBrandId
                  and p.id <> :productId
            """)
    boolean existsProductByBrand_User_ExternalUserIdAndIdNot(String productName, String generalBrandId, UUID productId);


}
