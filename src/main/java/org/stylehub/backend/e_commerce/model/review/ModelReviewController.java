package org.stylehub.backend.e_commerce.model.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Ratings", description = "Customer product ratings and model collaboration reviews.")
public class ModelReviewController {

    private final ModelReviewService modelReviewService;

    @Operation(summary = "List model reviews", description = "Returns paginated reviews received by the authenticated model profile.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Model reviews returned successfully"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot access this model profile"),
            @ApiResponse(responseCode = "404", description = "Model profile was not found")
    })
    @GetMapping
    public ResponseEntity<PageResponse<ModelReviewListItemResponse>> findModelReviews(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(this.modelReviewService.findModelReviews(pageable));
    }

    @Operation(summary = "Get model review stats", description = "Returns aggregate review statistics for the authenticated model profile.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Model review stats returned successfully"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot access this model profile"),
            @ApiResponse(responseCode = "404", description = "Model profile was not found")
    })
    @GetMapping("/stats")
    public ResponseEntity<ModelReviewStatsResponse> findModelReviewStats() {
        return ResponseEntity.ok(this.modelReviewService.getModelReviewStats());
    }
}
