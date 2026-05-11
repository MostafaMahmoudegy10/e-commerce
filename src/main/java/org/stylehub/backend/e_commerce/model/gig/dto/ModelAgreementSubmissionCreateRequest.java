package org.stylehub.backend.e_commerce.model.gig.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ModelAgreementSubmissionCreateRequest(
        String note,
        List<MultipartFile> files
) {
}
