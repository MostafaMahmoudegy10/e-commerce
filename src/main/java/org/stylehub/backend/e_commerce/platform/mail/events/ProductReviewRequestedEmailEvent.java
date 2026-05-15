package org.stylehub.backend.e_commerce.platform.mail.events;

import org.stylehub.backend.e_commerce.platform.mail.dto.ProductReviewEmailItem;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductReviewRequestedEmailEvent(
        UUID orderId,
        String orderNumber,
        String customerExternalId,
        String customerName,
        String customerEmail,
        List<ProductReviewEmailItem> products,
        Instant deliveredAt
) {
}
