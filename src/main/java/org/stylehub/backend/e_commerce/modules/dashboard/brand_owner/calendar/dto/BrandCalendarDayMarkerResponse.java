package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto;

public record BrandCalendarDayMarkerResponse(
        BrandCalendarEventType eventType,
        long count
) {
}
