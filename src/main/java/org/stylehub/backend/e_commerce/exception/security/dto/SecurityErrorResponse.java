package org.stylehub.backend.e_commerce.exception.security.dto;

import java.time.Instant;

public record SecurityErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
