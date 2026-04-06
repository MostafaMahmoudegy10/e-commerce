package org.stylehub.backend.e_commerce.user.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    MALE('M'),
    FEMALE('F');

    private final char code;

    // this method static to call it from class and to return the gender
    public static Gender fromCode(char code){
        if("M".equalsIgnoreCase(String.valueOf(code))){
            return MALE;
        }
        if("F".equalsIgnoreCase(String.valueOf(code))){
            return FEMALE;
        }
        throw new UnsupportedOperationException("The code " + code + " is not supported!");
    }
}
