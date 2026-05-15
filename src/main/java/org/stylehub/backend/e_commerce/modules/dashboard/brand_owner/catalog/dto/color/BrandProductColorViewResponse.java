package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.color;

import java.util.List;
import java.util.UUID;

public record BrandProductColorViewResponse(
        UUID colorId,
        String colorCode,
        List<String> imageUrls,
        Long variantsCount,
        Long totalStock
) {
}
