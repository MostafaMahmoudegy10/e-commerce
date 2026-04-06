package org.stylehub.backend.e_commerce.user.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, Character> {

    @Override
    public Character convertToDatabaseColumn(Gender gender) {
        if (gender == null) {
            return null;
        }
        return gender.getCode();
    }

    @Override
    public Gender convertToEntityAttribute(Character character) {
        if (character == null) {
            return null;
        }
        return Gender.fromCode(character);
    }
}
