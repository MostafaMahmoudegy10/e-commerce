package org.stylehub.backend.e_commerce.product.product_item.size;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "varchar(255)")
    private String sizeName;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "product_item_id")
    private ProductItem productItem;

    public void addToStock(Integer stock) {
        this.stock += stock;
    }

    public  void removeFromStock(Integer stock) {
        this.stock -= stock;
    }
}
