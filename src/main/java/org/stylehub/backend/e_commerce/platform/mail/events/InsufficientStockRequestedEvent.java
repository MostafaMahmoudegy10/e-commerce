package org.stylehub.backend.e_commerce.platform.mail.events;

import java.time.Instant;
import java.util.UUID;

public record InsufficientStockRequestedEvent(
        String brandOwnerName,
        String brandOwnerEmail,

        String productName,

        String sku,

        Integer requestedQuantity,
        Integer availableStock,

        String customerUserNamw,

        Instant occurredAt
) {}
