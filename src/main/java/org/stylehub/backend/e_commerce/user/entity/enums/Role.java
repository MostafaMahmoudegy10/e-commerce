package org.stylehub.backend.e_commerce.user.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    USER ("user"),
    BRAND("brand");

    private final String roleName;

    public static Role findByRoleName(String roleName){
        if(roleName.equalsIgnoreCase("user")){
            return USER;
        }
        if ("brand".equalsIgnoreCase(roleName)){
            return BRAND;
        }
        throw new UnsupportedOperationException(
                "this role"+ roleName+" does not exist"
        );
    }
}
