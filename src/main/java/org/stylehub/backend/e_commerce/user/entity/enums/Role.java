package org.stylehub.backend.e_commerce.user.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    CUSTOMER("customer"),
    BRAND_OWNER("brand_owner"),;

    private String value;

    public static Role findByValue(String value) {
        if(value==null||value.isEmpty()){
            return null;
        }
        if("CUSTOMER".equalsIgnoreCase(value)){
            return CUSTOMER;
        }
        if("BRAND_OWNER".equalsIgnoreCase(value)){
            return BRAND_OWNER;
        }
        return CUSTOMER;
    }
}
