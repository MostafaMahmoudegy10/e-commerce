package org.stylehub.backend.e_commerce.model.gig.dto;

import org.stylehub.backend.e_commerce.model.gig.entity.SubmissionAssetType;

import java.time.Instant;
import java.util.UUID;

public record GigAgreementSubmissionAssetViewResponse(
        UUID assetId,
        String assetUrl,
        String publicId,
        String mimeType,
        SubmissionAssetType assetType,
        Instant createdAt
) {
}
