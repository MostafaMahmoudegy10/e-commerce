package org.stylehub.backend.e_commerce.product.category.entity;

import org.springframework.core.convert.converter.Converter;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

public class GenderConverterRequest implements Converter<Character, Gender> {

    @Override
    public Gender convert(Character source) {
      return Gender.fromCode(source);
    }
}
