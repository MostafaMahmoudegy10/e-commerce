package org.stylehub.backend.e_commerce.model.review.dto;

public record ModelReviewUpsertRequest(
        Integer stars,
        String comment
) {
}
