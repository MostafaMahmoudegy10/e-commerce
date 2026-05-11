package org.stylehub.backend.e_commerce.model.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelAvailableForRow;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelAvailableForView;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelSearchFilterRequest;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelProfileImageRow;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelSearchResponse;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelSearchRow;
import org.stylehub.backend.e_commerce.model.profile.repository.ModelProfileAvailableForRepository;
import org.stylehub.backend.e_commerce.model.profile.repository.ModelProfileImagesRepository;
import org.stylehub.backend.e_commerce.model.profile.repository.ModelSearchRepository;
import org.stylehub.backend.e_commerce.platform.dto.PageResponse;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelSearchService {

    private final ModelSearchRepository modelSearchRepository;
    private final ModelProfileAvailableForRepository modelProfileAvailableForRepository;
    private final ModelProfileImagesRepository modelProfileImagesRepository;

    public PageResponse<ModelSearchResponse> searchModels(ModelSearchFilterRequest filter, Pageable pageable) {
        ModelSearchFilterRequest normalizedFilter = normalizeAndValidate(filter);

        Page<ModelSearchRow> resultPage = this.modelSearchRepository.searchModels(normalizedFilter, pageable);

        Map<UUID, String> profileImageByModelId = loadProfileImageByModelId(resultPage.getContent());
        Map<UUID, List<ModelAvailableForView>> availableForByModelId =
                loadAvailableForByModelId(resultPage.getContent());

        List<ModelSearchResponse> items = resultPage.getContent().stream()
                .map(row -> new ModelSearchResponse(
                        row.modelId(),
                        row.modelName(),
                        row.modelEmail(),
                        row.city(),
                        row.age(),
                        row.heightCm(),
                        row.weightKg(),
                        row.hairColor(),
                        row.bodyType(),
                        row.skinTone(),
                        row.gender(),
                        row.ratingAvg(),
                        row.ratingCount(),
                        row.isAvailable(),
                        profileImageByModelId.get(row.modelId()),
                        availableForByModelId.getOrDefault(row.modelId(), List.of())
                ))
                .toList();

        return new PageResponse<>(
                items,
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.hasNext(),
                resultPage.hasPrevious()
        );
    }

    private Map<UUID, String> loadProfileImageByModelId(List<ModelSearchRow> rows) {
        List<UUID> modelIds = rows.stream()
                .map(ModelSearchRow::modelId)
                .toList();

        if (modelIds.isEmpty()) {
            return Map.of();
        }

        List<ModelProfileImageRow> imageRows = this.modelProfileImagesRepository.findImagesByModelProfileIds(modelIds);

        Map<UUID, String> firstImageByModelId = new LinkedHashMap<>();
        imageRows.forEach(row -> firstImageByModelId.putIfAbsent(row.modelId(), row.profileImage()));
        return firstImageByModelId;
    }

    private Map<UUID, List<ModelAvailableForView>> loadAvailableForByModelId(List<ModelSearchRow> rows) {
        List<UUID> modelIds = rows.stream()
                .map(ModelSearchRow::modelId)
                .toList();

        if (modelIds.isEmpty()) {
            return Map.of();
        }

        List<ModelAvailableForRow> availableForRows = this.modelProfileAvailableForRepository
                .findAvailableForByModelProfileIds(modelIds);

        return availableForRows.stream()
                .collect(Collectors.groupingBy(
                        ModelAvailableForRow::modelId,
                        Collectors.mapping(
                                row -> new ModelAvailableForView(row.availableFor(), row.pricePerSession()),
                                Collectors.toList()
                        )
                ));
    }

    private ModelSearchFilterRequest normalizeAndValidate(ModelSearchFilterRequest filter) {
        ModelSearchFilterRequest safeFilter = filter == null
                ? new ModelSearchFilterRequest(null, null, null, null, null, null, null, List.of(), null)
                : filter;

        validateRange("age", safeFilter.minAge(), safeFilter.maxAge());
        validateRange("heightCm", safeFilter.minHeightCm(), safeFilter.maxHeightCm());
        validateRange("weightKg", safeFilter.minWeightKg(), safeFilter.maxWeightKg());

        return new ModelSearchFilterRequest(
                normalizeSearch(safeFilter.search()),
                safeFilter.minAge(),
                safeFilter.maxAge(),
                safeFilter.minHeightCm(),
                safeFilter.maxHeightCm(),
                safeFilter.minWeightKg(),
                safeFilter.maxWeightKg(),
                safeFilter.availableFor() == null ? List.of() : safeFilter.availableFor(),
                safeFilter.isAvailable()
        );
    }

    private String normalizeSearch(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        return search.trim();
    }

    private void validateRange(String fieldName, Integer min, Integer max) {
        if (min != null && max != null && min > max) {
            throw new IllegalArgumentException(fieldName + " min must be less than or equal to max");
        }
    }
}
