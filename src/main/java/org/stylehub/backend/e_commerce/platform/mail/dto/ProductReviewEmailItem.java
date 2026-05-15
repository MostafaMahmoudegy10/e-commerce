package org.stylehub.backend.e_commerce.platform.mail.dto;

import java.util.UUID;

public record ProductReviewEmailItem(
        UUID productId,
        String productName
) {
}
