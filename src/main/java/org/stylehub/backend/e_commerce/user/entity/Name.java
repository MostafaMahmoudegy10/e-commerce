package org.stylehub.backend.e_commerce.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Name {

    @Column(name = "first_name",columnDefinition = "varchar(255)", length = 255, nullable = false)
    private String firstName;

    @Column(name = "last_name",columnDefinition = "varchar(255)", length = 255, nullable = false)
    private String lastName;
}
