package org.stylehub.backend.e_commerce.model.profile.dto;

import java.util.List;

public record ModelAvailableForRequest(
        List<ModelAvailableForCreation> availableFor
) {
}
