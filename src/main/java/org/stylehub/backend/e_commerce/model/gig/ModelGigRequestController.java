package org.stylehub.backend.e_commerce.model.gig;

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
public class ModelGigRequestController {

    private final ModelGigRequestService modelGigRequestService;

    @GetMapping
    public ResponseEntity<PageResponse<ModelGigRequestViewResponse>> findMyRequests(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(name = "status", required = false) RequestStatus status
            ) {
        return ResponseEntity.ok(this.modelGigRequestService.findMyRequests(pageable,status));
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<ModelGigRequestDecisionResponse> acceptRequest(@PathVariable UUID requestId) {
        return ResponseEntity.ok(this.modelGigRequestService.acceptRequest(requestId));
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ModelGigRequestDecisionResponse> rejectRequest(
            @PathVariable UUID requestId,
            @RequestBody ModelGigRequestRejectRequest request
    ) {
        return ResponseEntity.ok(this.modelGigRequestService.rejectRequest(requestId, request));
    }
}
