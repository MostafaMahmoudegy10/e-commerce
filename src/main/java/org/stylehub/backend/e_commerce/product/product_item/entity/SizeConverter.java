package org.stylehub.backend.e_commerce.product.product_item.entity;

import jakarta.persistence.AttributeConverter;

public class SizeConverter implements AttributeConverter<Size, String> {

    @Override
    public String convertToDatabaseColumn(Size size) {
        if(size == null) return null;
        if (size.equals(Size.X_SMALL)) {return "xs";}
        if (size.equals(Size.SMALL)) {return "s";}
        if (size.equals(Size.MEDIUM)) {return "m";}
        if (size.equals(Size.LARGE)) {return "l";}
        if (size.equals(Size.X_LARGE)) {return "xl";}
        if (size.equals(Size.XX_LARGE)) {return "xxl";}
        throw new IllegalArgumentException("Invalid size value");
    }

    @Override
    public Size convertToEntityAttribute(String s) {
        return Size.findByValue(s);
    }
}
