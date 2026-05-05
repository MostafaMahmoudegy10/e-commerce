package org.stylehub.backend.e_commerce.platform.dto;

import java.util.List;

public record PageResponse<F>(
        List<?> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {}


