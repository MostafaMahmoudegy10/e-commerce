package org.stylehub.backend.e_commerce.model.gig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelGigRequestDecisionResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelGigRequestRejectRequest;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelGigRequestViewResponse;
import org.stylehub.backend.e_commerce.model.gig.entity.RequestStatus;
import org.stylehub.backend.e_commerce.model.gig.service.ModelGigRequestService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/model/requests")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Tag(name = "Brand Model Collaboration", description = "Manage collaboration requests, agreements, submissions, payments, and reviews between brands and models.")
public class ModelGigRequestController {

    private final ModelGigRequestService modelGigRequestService;

    @GetMapping
    public ResponseEntity<PageResponse<ModelGigRequestViewResponse>> findMyRequests(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(name = "status", required = false) RequestStatus status
            ) {
        return ResponseEntity.ok(this.modelGigRequestService.findMyRequests(pageable,status));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ModelGigRequestViewResponse> findMyRequestDetails(@PathVariable UUID requestId) {
        return ResponseEntity.ok(this.modelGigRequestService.findMyRequestDetails(requestId));
    }

    @Operation(summary = "Accept collaboration request", description = "Accepts a brand collaboration request and advances it toward an agreement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Collaboration request accepted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request state transition"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot accept this request"),
            @ApiResponse(responseCode = "404", description = "Collaboration request was not found")
    })
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<ModelGigRequestDecisionResponse> acceptRequest(@PathVariable UUID requestId) {
        return ResponseEntity.ok(this.modelGigRequestService.acceptRequest(requestId));
    }

    @Operation(summary = "Reject collaboration request", description = "Rejects a brand collaboration request with a model-provided reason.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Collaboration request rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request state transition or rejection data"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user cannot reject this request"),
            @ApiResponse(responseCode = "404", description = "Collaboration request was not found")
    })
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ModelGigRequestDecisionResponse> rejectRequest(
            @PathVariable UUID requestId,
            @RequestBody ModelGigRequestRejectRequest request
    ) {
        return ResponseEntity.ok(this.modelGigRequestService.rejectRequest(requestId, request));
    }
}
