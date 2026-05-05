package org.stylehub.backend.e_commerce.customer.dto.rating;

public record ProductRatingCreation(
        Integer stars,
        String comment
) {
}
