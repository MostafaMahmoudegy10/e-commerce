package org.stylehub.backend.e_commerce.cart.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

public enum CartStatus {
    ACTIVE,//IT IS ACTIVE
    CHECKED_OUT, // CONVERTED TO CHECKOUT
    ABANDONED, // USER ADD TO CART AND LEFT IT  A LONG TIME
    EXPIRED // EXPIRED
}