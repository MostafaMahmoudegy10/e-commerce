package org.stylehub.backend.e_commerce.model.review;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.model.review.dto.ModelReviewListItemResponse;
import org.stylehub.backend.e_commerce.model.review.dto.ModelReviewStatsResponse;
import org.stylehub.backend.e_commerce.model.review.service.ModelReviewService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

@RestController
@RequestMapping("api/v1/model/reviews")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class ModelReviewController {

    private final ModelReviewService modelReviewService;

    @GetMapping
    public ResponseEntity<PageResponse<ModelReviewListItemResponse>> findModelReviews(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(this.modelReviewService.findModelReviews(pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<ModelReviewStatsResponse> findModelReviewStats() {
        return ResponseEntity.ok(this.modelReviewService.getModelReviewStats());
    }
}
