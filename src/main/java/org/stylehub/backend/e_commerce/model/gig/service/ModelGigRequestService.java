package org.stylehub.backend.e_commerce.model.gig.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.brand.entity.Brand;
import org.stylehub.backend.e_commerce.brand.service.BrandService;
import org.stylehub.backend.e_commerce.model.gig.dto.BrandGigRequestViewResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelGigRequestDecisionResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelGigRequestCreationRequest;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelGigRequestCreationResponse;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelGigRequestRejectRequest;
import org.stylehub.backend.e_commerce.model.gig.dto.ModelGigRequestViewResponse;
import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelAgreement;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelGigRequest;
import org.stylehub.backend.e_commerce.model.gig.entity.RequestStatus;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestAcceptedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestCancelledEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestCreatedEvent;
import org.stylehub.backend.e_commerce.model.gig.event.ModelGigRequestRejectedEvent;
import org.stylehub.backend.e_commerce.model.gig.publisher.ModelGigRequestEventPublisher;
import org.stylehub.backend.e_commerce.model.gig.repository.ModelAgreementRepository;
import org.stylehub.backend.e_commerce.model.gig.repository.ModelGigRequestRepository;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfile;
import org.stylehub.backend.e_commerce.model.profile.repository.ModelProfileRepository;
import org.stylehub.backend.e_commerce.order.payment.entity.PaymentStatus;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModelGigRequestService {

    private final ModelGigRequestRepository modelGigRequestRepository;
    private final ModelAgreementRepository modelAgreementRepository;
    private final ModelProfileRepository modelProfileRepository;
    private final BrandService brandService;
    private final CurrentUserProvider currentUserProvider;
    private final ModelGigRequestEventPublisher modelGigRequestEventPublisher;
    private final ModelAgreementPaymentService modelAgreementPaymentService;

    @Transactional
    public ModelGigRequestCreationResponse createRequest(UUID modelId, ModelGigRequestCreationRequest request) {
        validateRequest(request);

        Brand brand = this.brandService.findBrandByExternalId(currentUserProvider.externalId());
        ModelProfile modelProfile = this.modelProfileRepository.findById(modelId)
                .orElseThrow(() -> new IllegalArgumentException("Model profile not found"));

        ModelGigRequest modelGigRequest = new ModelGigRequest();
        modelGigRequest.setBrand(brand);
        modelGigRequest.setModelProfile(modelProfile);
        modelGigRequest.setAvailableFor(request.availableFor());
        modelGigRequest.setTitle(request.title().trim());
        modelGigRequest.setDescription(request.description().trim());
        modelGigRequest.setProposedPrice(request.proposedPrice());
        modelGigRequest.setDeadline(request.deadline());
        modelGigRequest.setLocation(normalizeLocation(request.location()));

        ModelGigRequest savedRequest = this.modelGigRequestRepository.save(modelGigRequest);

        this.modelGigRequestEventPublisher.publishRequestCreated(
                new ModelGigRequestCreatedEvent(
                        savedRequest.getId(),
                        savedRequest.getRequestNumber(),
                        brand.getId(),
                        brand.getUser().getId(),
                        brand.getBrandName(),
                        modelProfile.getId(),
                        modelProfile.getUser().getId(),
                        savedRequest.getAvailableFor(),
                        savedRequest.getTitle(),
                        savedRequest.getDescription(),
                        savedRequest.getProposedPrice(),
                        savedRequest.getDeadline(),
                        savedRequest.getLocation(),
                        savedRequest.getRequestStatus(),
                        savedRequest.getCreatedAt()
                )
        );

        return new ModelGigRequestCreationResponse(
                savedRequest.getId(),
                savedRequest.getRequestNumber(),
                brand.getId(),
                modelProfile.getId(),
                savedRequest.getAvailableFor(),
                savedRequest.getTitle(),
                savedRequest.getDescription(),
                savedRequest.getProposedPrice(),
                savedRequest.getDeadline(),
                savedRequest.getLocation(),
                savedRequest.getRequestStatus(),
                savedRequest.getCreatedAt()
        );
    }

    public PageResponse<ModelGigRequestViewResponse> findMyRequests(Pageable pageable, RequestStatus status) {
        Page<ModelGigRequest> page = this.modelGigRequestRepository.findAllByModelExternalId(
                currentUserProvider.externalId(),
                status,
                pageable
        );

        List<ModelGigRequestViewResponse> items = page.getContent().stream()
                .map(this::mapToViewResponse)
                .toList();

        return new PageResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public ModelGigRequestViewResponse findMyRequestDetails(UUID requestId) {
        return mapToViewResponse(findOwnedRequest(requestId));
    }

    public PageResponse<BrandGigRequestViewResponse> findBrandRequests(Pageable pageable, RequestStatus status) {
        Page<ModelGigRequest> page = this.modelGigRequestRepository.findAllByBrandExternalId(
                currentUserProvider.externalId(),
                status,
                pageable
        );

        List<BrandGigRequestViewResponse> items = page.getContent().stream()
                .map(this::mapToBrandViewResponse)
                .toList();

        return new PageResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public BrandGigRequestViewResponse findBrandRequestDetails(UUID requestId) {
        return mapToBrandViewResponse(findBrandOwnedRequest(requestId));
    }

    @Transactional
    public ModelGigRequestDecisionResponse acceptRequest(UUID requestId) {
        ModelGigRequest request = findOwnedRequest(requestId);
        ensurePending(request);

        request.setRequestStatus(RequestStatus.ACCEPTED);
        request.setRespondedAt(Instant.now());

        ModelAgreement agreement = new ModelAgreement();
        agreement.setBrand(request.getBrand());
        agreement.setModelProfile(request.getModelProfile());
        agreement.setRequest(request);
        agreement.setAvailableFor(request.getAvailableFor());
        agreement.setTitle(request.getTitle());
        agreement.setDescription(request.getDescription());
        agreement.setAgreedPrice(request.getProposedPrice());
        agreement.setDeadline(request.getDeadline());
        agreement.setLocation(request.getLocation());
        agreement.setAgreementStatus(AgreementStatus.IN_PROGRESS);
        agreement.setPaymentStatus(PaymentStatus.PENDING);
        agreement.setAcceptedAt(request.getRespondedAt());

        ModelAgreement savedAgreement = this.modelAgreementRepository.save(agreement);
        this.modelAgreementPaymentService.createPendingPayment(savedAgreement);
        ModelGigRequest savedRequest = this.modelGigRequestRepository.save(request);

        this.modelGigRequestEventPublisher.publishRequestAccepted(
                new ModelGigRequestAcceptedEvent(
                        savedRequest.getId(),
                        savedRequest.getRequestNumber(),
                        savedRequest.getBrand().getId(),
                        savedRequest.getBrand().getUser().getId(),
                        savedRequest.getBrand().getBrandName(),
                        savedRequest.getModelProfile().getId(),
                        savedRequest.getModelProfile().getUser().getId(),
                        savedAgreement.getId(),
                        savedAgreement.getAgreementNumber(),
                        savedRequest.getRequestStatus(),
                        savedAgreement.getAgreementStatus(),
                        savedAgreement.getPaymentStatus(),
                        savedRequest.getRespondedAt()
                )
        );

        return new ModelGigRequestDecisionResponse(
                savedRequest.getId(),
                savedRequest.getRequestNumber(),
                savedRequest.getRequestStatus(),
                savedRequest.getRespondedAt(),
                savedAgreement.getId(),
                savedAgreement.getAgreementNumber(),
                savedAgreement.getAgreementStatus(),
                savedAgreement.getPaymentStatus()
        );
    }

    @Transactional
    public ModelGigRequestDecisionResponse cancelRequest(UUID requestId) {
        ModelGigRequest request = findBrandOwnedRequest(requestId);
        ensurePending(request);

        request.setRequestStatus(RequestStatus.CANCELLED);
        request.setRespondedAt(Instant.now());

        ModelGigRequest savedRequest = this.modelGigRequestRepository.save(request);

        this.modelGigRequestEventPublisher.publishRequestCancelled(
                new ModelGigRequestCancelledEvent(
                        savedRequest.getId(),
                        savedRequest.getRequestNumber(),
                        savedRequest.getBrand().getId(),
                        savedRequest.getBrand().getUser().getId(),
                        savedRequest.getBrand().getBrandName(),
                        savedRequest.getModelProfile().getId(),
                        savedRequest.getModelProfile().getUser().getId(),
                        savedRequest.getRequestStatus(),
                        savedRequest.getRespondedAt()
                )
        );

        return new ModelGigRequestDecisionResponse(
                savedRequest.getId(),
                savedRequest.getRequestNumber(),
                savedRequest.getRequestStatus(),
                savedRequest.getRespondedAt(),
                null,
                null,
                null,
                null
        );
    }

    @Transactional
    public ModelGigRequestDecisionResponse rejectRequest(UUID requestId, ModelGigRequestRejectRequest rejectRequest) {
        if (rejectRequest == null || rejectRequest.rejectionReason() == null || rejectRequest.rejectionReason().isBlank()) {
            throw new IllegalArgumentException("rejectionReason is required");
        }

        ModelGigRequest request = findOwnedRequest(requestId);
        ensurePending(request);

        request.setRequestStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectRequest.rejectionReason().trim());
        request.setRespondedAt(Instant.now());

        ModelGigRequest savedRequest = this.modelGigRequestRepository.save(request);

        this.modelGigRequestEventPublisher.publishRequestRejected(
                new ModelGigRequestRejectedEvent(
                        savedRequest.getId(),
                        savedRequest.getRequestNumber(),
                        savedRequest.getBrand().getId(),
                        savedRequest.getBrand().getUser().getId(),
                        savedRequest.getBrand().getBrandName(),
                        savedRequest.getModelProfile().getId(),
                        savedRequest.getModelProfile().getUser().getId(),
                        savedRequest.getRequestStatus(),
                        savedRequest.getRejectionReason(),
                        savedRequest.getRespondedAt()
                )
        );

        return new ModelGigRequestDecisionResponse(
                savedRequest.getId(),
                savedRequest.getRequestNumber(),
                savedRequest.getRequestStatus(),
                savedRequest.getRespondedAt(),
                null,
                null,
                null,
                null
        );
    }

    private void validateRequest(ModelGigRequestCreationRequest request) {
        if (request.availableFor() == null) {
            throw new IllegalArgumentException("availableFor is required");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (request.description() == null || request.description().isBlank()) {
            throw new IllegalArgumentException("description is required");
        }
        if (request.proposedPrice() == null) {
            throw new IllegalArgumentException("proposedPrice is required");
        }
        if (request.proposedPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("proposedPrice must be greater than zero");
        }
    }

    private String normalizeLocation(String location) {
        if (location == null || location.isBlank()) {
            return null;
        }
        return location.trim();
    }

    private ModelGigRequest findOwnedRequest(UUID requestId) {
        return this.modelGigRequestRepository.findByIdAndModelExternalId(requestId, currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Model gig request not found"));
    }

    private ModelGigRequest findBrandOwnedRequest(UUID requestId) {
        return this.modelGigRequestRepository.findByIdAndBrandExternalId(requestId, currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Brand gig request not found"));
    }

    private void ensurePending(ModelGigRequest request) {
        if (request.getRequestStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be updated");
        }
    }

    private ModelGigRequestViewResponse mapToViewResponse(ModelGigRequest request) {
        return new ModelGigRequestViewResponse(
                request.getId(),
                request.getRequestNumber(),
                request.getBrand().getId(),
                request.getBrand().getBrandName(),
                request.getBrand().getBrandImageUrl(),
                request.getAvailableFor(),
                request.getTitle(),
                request.getDescription(),
                request.getProposedPrice(),
                request.getDeadline(),
                request.getLocation(),
                request.getRequestStatus(),
                request.getRejectionReason(),
                request.getCreatedAt(),
                request.getRespondedAt()
        );
    }

    private BrandGigRequestViewResponse mapToBrandViewResponse(ModelGigRequest request) {
        return new BrandGigRequestViewResponse(
                request.getId(),
                request.getRequestNumber(),
                request.getModelProfile().getId(),
                request.getModelProfile().getModelName(),
                request.getModelProfile().getModelEmail(),
                request.getAvailableFor(),
                request.getTitle(),
                request.getDescription(),
                request.getProposedPrice(),
                request.getDeadline(),
                request.getLocation(),
                request.getRequestStatus(),
                request.getRejectionReason(),
                request.getCreatedAt(),
                request.getRespondedAt()
        );
    }
}
