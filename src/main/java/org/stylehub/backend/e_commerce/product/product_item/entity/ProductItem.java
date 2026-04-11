package org.stylehub.backend.e_commerce.product.product_item.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductItemImage;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.product_item.size.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class ProductItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "color", nullable = false,columnDefinition = "varchar(20)")
    private String color;

    @Column(length = 30, nullable = false,columnDefinition = "varchar(30)")
    private String sku;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "productItem",
    cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE})
    private List<ProductItemImage> productItemImages;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product_item_id")
    private List<Size>sizeList;

    @Override
    public String toString() {
        return "ProductItem{" +
                "color='" + color + '\'' +
                ", sku='" + sku + '\'' +
                '}';
    }
}
