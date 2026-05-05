package org.stylehub.backend.e_commerce.customer.rating.product_rating_summary.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.product.entity.Product;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {@UniqueConstraint(name = "uq_product_avg_rate",columnNames = {"product_id","avg_rating"})}
)
public class ProductRatingSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal avgRating;

    private Integer ratingCount;

    private Integer rating1Count;

    private Integer rating2Count;

    private Integer rating3Count;

    private Integer rating4Count;

    private Integer rating5Count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @Override
    public String toString() {
        return "ProductRatingSummary{" +
                "id=" + id +
                ", avgRating=" + avgRating +
                ", ratingCount=" + ratingCount +
                ", rating1Count=" + rating1Count +
                ", rating2Count=" + rating2Count +
                ", rating3Count=" + rating3Count +
                ", rating4Count=" + rating4Count +
                ", rating5Count=" + rating5Count +
                '}';
    }
}
