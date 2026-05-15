package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto;

import java.time.LocalDate;
import java.util.List;

public record BrandCalendarDayResponse(
        LocalDate date,
        List<BrandCalendarEventResponse> events
) {
}
