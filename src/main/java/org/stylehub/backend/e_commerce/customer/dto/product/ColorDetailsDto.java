package org.stylehub.backend.e_commerce.customer.dto.product;

import java.util.List;
import java.util.UUID;

public record ColorDetailsDto(
        UUID productColorId,
        String colorCode,
        List<String> colorImages,
        List<VariantsDetailsDto>variantsDetailsDtos
) {

}
