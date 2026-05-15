package org.stylehub.backend.e_commerce.model.gig.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.model.gig.dto.GigAgreementViewResponse;
import org.stylehub.backend.e_commerce.model.gig.entity.AgreementStatus;
import org.stylehub.backend.e_commerce.model.gig.entity.ModelAgreement;
import org.stylehub.backend.e_commerce.model.gig.repository.ModelAgreementRepository;
import org.stylehub.backend.e_commerce.model.profile.service.ModelProfileAccessService;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModelAgreementService {

    private final ModelAgreementRepository modelAgreementRepository;
    private final ModelProfileAccessService modelProfileAccessService;
    private final CurrentUserProvider currentUserProvider;

    public PageResponse<GigAgreementViewResponse> findModelAgreements(Pageable pageable, AgreementStatus status) {
        this.modelProfileAccessService.requireCurrentModelProfile();

        Page<ModelAgreement> page = this.modelAgreementRepository.findAllByModelExternalId(
                currentUserProvider.externalId(),
                status,
                pageable
        );

        List<GigAgreementViewResponse> items = page.getContent().stream()
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

    public PageResponse<GigAgreementViewResponse> findBrandAgreements(Pageable pageable, AgreementStatus status) {
        Page<ModelAgreement> page = this.modelAgreementRepository.findAllByBrandExternalId(
                currentUserProvider.externalId(),
                status,
                pageable
        );

        List<GigAgreementViewResponse> items = page.getContent().stream()
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

    public GigAgreementViewResponse findModelAgreementDetails(UUID agreementId) {
        this.modelProfileAccessService.requireCurrentModelProfile();

        ModelAgreement agreement = this.modelAgreementRepository
                .findByIdAndModelExternalId(agreementId, currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Agreement not found"));
        return mapToViewResponse(agreement);
    }

    public GigAgreementViewResponse findBrandAgreementDetails(UUID agreementId) {
        ModelAgreement agreement = this.modelAgreementRepository
                .findByIdAndBrandExternalId(agreementId, currentUserProvider.externalId())
                .orElseThrow(() -> new IllegalArgumentException("Agreement not found"));
        return mapToViewResponse(agreement);
    }

    private GigAgreementViewResponse mapToViewResponse(ModelAgreement agreement) {
        return new GigAgreementViewResponse(
                agreement.getId(),
                agreement.getAgreementNumber(),
                agreement.getRequest().getId(),
                agreement.getRequest().getRequestNumber(),
                agreement.getBrand().getId(),
                agreement.getBrand().getBrandName(),
                agreement.getBrand().getBrandImageUrl(),
                agreement.getModelProfile().getId(),
                agreement.getModelProfile().getModelName(),
                agreement.getModelProfile().getModelEmail(),
                agreement.getAvailableFor(),
                agreement.getTitle(),
                agreement.getDescription(),
                agreement.getAgreedPrice(),
                agreement.getDeadline(),
                agreement.getLocation(),
                agreement.getAgreementStatus(),
                agreement.getPaymentStatus(),
                agreement.getCreatedAt(),
                agreement.getAcceptedAt(),
                agreement.getDeliveredAt(),
                agreement.getCompletedAt()
        );
    }
}
