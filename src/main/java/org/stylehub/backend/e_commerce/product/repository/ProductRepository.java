package org.stylehub.backend.e_commerce.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.customer.dto.product.ProductSummary;
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


    @Query(value = """
         select  p.product_name_en,p.product_name_ar,p.id,p.thumbnail from Product p
            inner join brand b
            on b.id=p.brand_id
            inner join users u
            on u.id=b.user_id
                 where p.search_vector @@ websearch_to_tsquery('simple',:search)
                         and u.external_user_id=:brandId
                 ORDER BY ts_rank(p.search_vector, websearch_to_tsquery('simple', :search)) DESC
        """,nativeQuery = true
    ,countQuery = """
        SELECT COUNT(*)
        FROM product p
        WHERE p.search_vector @@ websearch_to_tsquery('simple', :search);
        """)
    Page<Object[]> findProductSummary(String search, Pageable pageable,String brandId);

    @Query(value = """
          select new   org.stylehub.backend.e_commerce.customer.dto.product.ProductSummary(
                p.productNameAr,
                p.productNameEn,
                p.id,
                p.thumbnail                  
                  ) from Product p
          where p.brand.id=:brandId
        """,countQuery = """
            select count(p.id) from Product p
            where p.brand.id=:brandId        
        """)
    Page<ProductSummary> findAllProductsForBrand(UUID brandId, Pageable pageable);
}
