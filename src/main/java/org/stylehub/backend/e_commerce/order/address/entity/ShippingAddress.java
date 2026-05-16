package org.stylehub.backend.e_commerce.order.address.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.order.entity.Order;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "shipping_address")
public class ShippingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,name = "city_en")
    private String cityEn;

    @Column(nullable = false,name = "city_ar")
    private String cityAr;

    @Column(nullable = false,name = "street_en")
    private String streetEn;

    @Column(nullable = false,name = "street_ar")
    private String streetAr;

    @Column(nullable = false,name = "building_number")
    private String buildingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id",nullable = false)
    private CustomerProfile customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    public String addressEn(String streetEn,String cityEn,String buildingNumber){
         return streetEn+" "+cityEn+" "+buildingNumber;
    }


    public String addressAr(String streetAr,String cityAr,String buildingNumber){
        return streetAr+" "+cityAr+" "+buildingNumber;
    }

    @Override
    public String toString() {
        return "ShippingAddress{" +
                "cityEn='" + cityEn + '\'' +
                ", cityAr='" + cityAr + '\'' +
                ", streetEn='" + streetEn + '\'' +
                ", streetAr='" + streetAr + '\'' +
                ", buildingNumber='" + buildingNumber + '\'' +
                '}';
    }
}
