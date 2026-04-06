package org.stylehub.backend.e_commerce.admins.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class AdminComposedPK {

    private UUID brandId;

    private UUID userId;
}
