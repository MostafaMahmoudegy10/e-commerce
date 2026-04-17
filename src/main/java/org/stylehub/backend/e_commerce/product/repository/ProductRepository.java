package org.stylehub.backend.e_commerce.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.math.BigDecimal;
import java.util.List;
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

    @Query("""
            select p
            from Product p
            where p.isActive = true
              and p.isArchived = false
              and (:brandId is null or p.brand.id = :brandId)
              and (:categoryId is null or p.category.id = :categoryId)
              and (:queryText is null
                   or lower(p.productNameEn) like lower(concat('%', :queryText, '%'))
                   or lower(p.productNameAr) like lower(concat('%', :queryText, '%')))
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
              and (:minRating is null or (
                    select coalesce(avg(pr.rating), 0)
                    from ProductReview pr
                    where pr.product = p
                  ) >= :minRating)
            """)
    Page<Product> findAllForCustomer(
            UUID brandId,
            UUID categoryId,
            String queryText,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Double minRating,
            Pageable pageable
    );

    List<Product> findTop20ByIsActiveTrueAndIsArchivedFalseOrderByIdDesc();

    List<Product> findTop20ByCategory_IdAndIsActiveTrueAndIsArchivedFalseOrderByIdDesc(UUID categoryId);
}
