package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto.BrandCalendarDayResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.dto.BrandCalendarMonthResponse;
import org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.calendar.service.BrandCalendarService;

@RestController
@RequestMapping("api/v1/brands/calendar")
@PreAuthorize("hasRole('BRAND_OWNER')")
@RequiredArgsConstructor
public class BrandOwnerCalendarController {

    private final BrandCalendarService brandCalendarService;

    @GetMapping
    public ResponseEntity<BrandCalendarMonthResponse> getMonthCalendar(
            @RequestParam(required = false) String month
    ) {
        return ResponseEntity.ok(this.brandCalendarService.getMonthCalendar(month));
    }

    @GetMapping("/day")
    public ResponseEntity<BrandCalendarDayResponse> getDayCalendar(
            @RequestParam(required = false) String date
    ) {
        return ResponseEntity.ok(this.brandCalendarService.getDayCalendar(date));
    }
}
