package org.stylehub.backend.e_commerce.modules.customer.catalog.dto;

public record ProductReviewRequest(
        Integer rating,
        String comment
) {
}
