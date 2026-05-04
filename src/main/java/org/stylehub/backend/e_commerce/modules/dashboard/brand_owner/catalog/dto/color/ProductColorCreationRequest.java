package org.stylehub.backend.e_commerce.modules.dashboard.brand_owner.catalog.dto.color;

import jakarta.mail.Multipart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ProductColorCreationRequest(
        String colorCode,
        List<MultipartFile> colorImages
) {
}
