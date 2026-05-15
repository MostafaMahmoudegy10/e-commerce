package org.stylehub.backend.e_commerce.customer.rating.product_rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.customer.rating.product_rating.entity.ProductRating;
import org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.dto.CalculateSummaryDto;

import java.util.Optional;
import java.util.UUID;

public interface ProductRatingRepository extends JpaRepository<ProductRating, UUID> {

    @Query("""
        select pr from ProductRating  pr
        join pr.customer c
        join pr.product p
        where c.id=:customerId and
              p.id=:productId
        """)
    Optional<ProductRating>findProductRatingByCustomer_IdAndProduct_Id(UUID customerId, UUID productId);

    @Query("""
       select new org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.dto.CalculateSummaryDto(
           cast(count(pr.id) as long),
           coalesce(avg(pr.stars), 0.0),
           coalesce(sum(case when pr.stars = 1 then 1L else 0L end), 0L),
           coalesce(sum(case when pr.stars = 2 then 1L else 0L end), 0L),
           coalesce(sum(case when pr.stars = 3 then 1L else 0L end), 0L),
           coalesce(sum(case when pr.stars = 4 then 1L else 0L end), 0L),
           coalesce(sum(case when pr.stars = 5 then 1L else 0L end), 0L)
       )
   from ProductRating pr
   where pr.product.id = :productId
""")
    CalculateSummaryDto calculateSummary(@Param("productId") UUID productId);

    void deleteAllByProduct_Id(UUID productId);
}
