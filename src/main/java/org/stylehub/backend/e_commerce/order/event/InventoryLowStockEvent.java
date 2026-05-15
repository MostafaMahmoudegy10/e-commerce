package org.stylehub.backend.e_commerce.order.event;

import java.time.Instant;
import java.util.UUID;

public record InventoryLowStockEvent(
        UUID brandUserId,
        UUID productId,
        String productName,
        String sku,
        Integer remainingStock,
        Instant occurredAt
) {
}
