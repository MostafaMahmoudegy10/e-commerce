package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto;

import java.time.YearMonth;
import java.util.List;

public record BrandCalendarMonthResponse(
        YearMonth month,
        List<BrandCalendarDaySummaryResponse> days,
        List<BrandCalendarUpcomingOrderResponse> upcomingOrders
) {
}
