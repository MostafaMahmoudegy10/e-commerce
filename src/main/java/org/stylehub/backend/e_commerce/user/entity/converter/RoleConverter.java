package org.stylehub.backend.e_commerce.user.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.stylehub.backend.e_commerce.user.entity.enums.Role;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
       if(role==null){
           return null;
       }
        return role.getRoleName();
    }

    @Override
    public Role convertToEntityAttribute(String roleName) {
        if (roleName == null) {
            return null;
        }
        return Role.findByRoleName(roleName);
    }
}
