package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto;

import java.time.LocalDate;
import java.util.List;

public record BrandCalendarDaySummaryResponse(
        LocalDate date,
        long totalEvents,
        List<BrandCalendarDayMarkerResponse> markers
) {
}
