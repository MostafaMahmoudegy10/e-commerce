package org.stylehub.backend.e_commerce.product.product_item.entity;

import lombok.Getter;

@Getter
public enum Size {
    X_SMALL("xs"),
    SMALL("s"),
    MEDIUM("m"),
    LARGE("l"),
    X_LARGE("xl"),
    XX_LARGE("xxl");

    private String value;

    Size(String value) {
        this.value = value;
    }

    public static Size findByValue(String value) {
        if(value.equals(null)){
            return null;
        }
        else if ("xs".equalsIgnoreCase(value)) {
            return Size.X_SMALL;
        }else if("small".equalsIgnoreCase(value)) {
            return Size.SMALL;
        }else if("medium".equalsIgnoreCase(value)) {
            return Size.MEDIUM;
        }else if("large".equalsIgnoreCase(value)) {
            return Size.LARGE;
        }else if("xl".equalsIgnoreCase(value)) {
            return Size.X_LARGE;
        }else if("xxl".equalsIgnoreCase(value)) {
            return Size.XX_LARGE;
        }
        throw new IllegalArgumentException("Invalid size ");
    }



}
