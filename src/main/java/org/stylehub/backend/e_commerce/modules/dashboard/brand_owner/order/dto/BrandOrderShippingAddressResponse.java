package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.order.dto;

public record BrandOrderShippingAddressResponse(
        String cityEn,
        String cityAr,
        String streetEn,
        String streetAr,
        String buildingNumber,
        String formattedAddressEn,
        String formattedAddressAr
) {
}
