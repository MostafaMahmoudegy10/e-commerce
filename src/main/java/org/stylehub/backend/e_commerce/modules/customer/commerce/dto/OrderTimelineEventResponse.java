package org.stylehub.backend.e_commerce.modules.customer.commerce.dto;

import java.sql.Timestamp;

public record OrderTimelineEventResponse(
        String eventType,
        Timestamp eventAt
) {
}
