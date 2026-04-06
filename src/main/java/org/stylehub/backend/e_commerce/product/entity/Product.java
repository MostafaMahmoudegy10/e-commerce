package org.stylehub.backend.e_commerce.product.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,name = "product_name")
    private String productName;

    @Column(nullable = false,name = "product_description")
    private String productDescription;

    @Column(name = "thumbnail",nullable = false)
    private String thumbnail;

    @Column(name = "public_id",nullable = false)
    private String publicId;

    @OneToMany(mappedBy = "product",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE})
    private List<ProductItem>  productItems;

    @Override
    public String toString() {
        return "Product{" +
                "publicId='" + publicId + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", productName='" + productName + '\'' +
                ", id=" + id +
                '}';
    }
}
