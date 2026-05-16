package org.stylehub.backend.e_commerce.model.profile.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.customer.profile.entity.CustomerProfile;
import org.stylehub.backend.e_commerce.customer.profile.service.CustomerProfileService;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelAvailableForRequest;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelAvailableForView;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelCreationRequest;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelCreationResponse;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelCustomerSummaryResponse;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelProfileImageRow;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelProfileResponse;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfile;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfileAvailableFor;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfileImages;
import org.stylehub.backend.e_commerce.model.profile.repository.ModelProfileAvailableForRepository;
import org.stylehub.backend.e_commerce.model.profile.repository.ModelProfileImagesRepository;
import org.stylehub.backend.e_commerce.model.profile.repository.ModelProfileRepository;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;
import org.stylehub.backend.e_commerce.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelService {

    private final ModelProfileRepository modelProfileRepository;
    private final ModelProfileAvailableForRepository modelProfileAvailableForRepository;
    private final ModelProfileImagesRepository modelProfileImagesRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CustomerProfileService customerProfileService;
    private final ImageService imageService;
    private final ModelProfileAccessService modelProfileAccessService;

    public ModelProfileResponse getCurrentModelProfile() {
        ModelProfile modelProfile = this.modelProfileAccessService.requireCurrentModelProfile();
        CustomerProfile customerProfile =
                this.customerProfileService.findCustomerProfileByExternalUserId(this.currentUserProvider.externalId());

        List<String> modelImages = this.modelProfileImagesRepository
                .findImagesByModelProfileIds(List.of(modelProfile.getId()))
                .stream()
                .map(ModelProfileImageRow::profileImage)
                .toList();

        List<ModelAvailableForView> availableFor = this.modelProfileAvailableForRepository
                .findAvailableForByModelProfileIds(List.of(modelProfile.getId()))
                .stream()
                .map(row -> new ModelAvailableForView(row.availableFor(), row.pricePerSession()))
                .toList();

        return new ModelProfileResponse(
                modelProfile.getId(),
                modelProfile.getModelName(),
                modelProfile.getModelEmail(),
                modelProfile.getBio(),
                modelProfile.getCity(),
                modelProfile.getAge(),
                modelProfile.getHeightCm(),
                modelProfile.getWeightKg(),
                modelProfile.getHairColor(),
                modelProfile.getRatingAvg(),
                modelProfile.getRatingCount(),
                modelProfile.getIsAvailable(),
                modelProfile.getBodyType(),
                modelProfile.getSkinTone(),
                modelProfile.getGender(),
                modelImages,
                availableFor,
                new ModelCustomerSummaryResponse(
                        customerProfile.getId(),
                        customerProfile.getUsername(),
                        customerProfile.getFirstName(),
                        customerProfile.getLastName(),
                        customerProfile.getCustomerEmail(),
                        customerProfile.getPhoneNumber(),
                        customerProfile.getProfileImageUrl()
                )
        );
    }

    @Transactional
    public ModelCreationResponse createModel(ModelCreationRequest request) {
        validateModelCreationRequest(request);

        CustomerProfile customerProfile =
                this.customerProfileService.findCustomerProfileByExternalUserId(currentUserProvider.externalId());

        User user = customerProfile.getUser();

        this.modelProfileRepository.findModelProfileByUser_Id(user.getId())
                .ifPresent(model -> {
                    throw new IllegalStateException("Model Profile already exists");
                });

        ModelProfile modelProfile = new ModelProfile();
        modelProfile.setUser(user);
        modelProfile.setAge(request.age());
        modelProfile.setBodyType(request.bodyType());
        modelProfile.setGender(customerProfile.getGender());
        modelProfile.setBio(request.bio());
        modelProfile.setCity(request.city());
        modelProfile.setModelEmail(customerProfile.getCustomerEmail());
        modelProfile.setModelName(customerProfile.getUsername());
        modelProfile.setHairColor(request.hairColor());
        modelProfile.setHeightCm(request.heightCm());
        modelProfile.setSkinTone(request.skinTone());
        modelProfile.setWeightKg(request.weightKg());

        ModelProfile savedModelProfile = this.modelProfileRepository.save(modelProfile);

        addImages(request.files(), savedModelProfile);

        return new ModelCreationResponse(
                savedModelProfile.getModelEmail(),
                savedModelProfile.getId(),
                savedModelProfile.getBodyType(),
                savedModelProfile.getSkinTone(),
                savedModelProfile.getRatingAvg()
        );
    }

    @Transactional
    public void addAvailableFor(ModelAvailableForRequest request) {
        validateAvailableForRequest(request);

        ModelProfile modelProfile = this.modelProfileAccessService.requireCurrentModelProfile();

        List<ModelProfileAvailableFor> availableForList = request.availableFor().stream()
                .map(availableForCreation -> {
                    ModelProfileAvailableFor modelProfileAvailableFor = new ModelProfileAvailableFor();
                    modelProfileAvailableFor.setModelProfile(modelProfile);
                    modelProfileAvailableFor.setAvailableFor(availableForCreation.availableFor());
                    modelProfileAvailableFor.setPricePerSession(availableForCreation.pricePerSession());
                    return modelProfileAvailableFor;
                })
                .collect(Collectors.toList());

        this.modelProfileAvailableForRepository.saveAll(availableForList);
    }

    private void addImages(List<MultipartFile> files, ModelProfile modelProfile) {
        List<ModelProfileImages> modelProfileImages = files.stream()
                .map(image -> {
                    UploadResponse response = imageService.uploadImageAsync(image).join();
                    ModelProfileImages newModelProfileImages = new ModelProfileImages();
                    newModelProfileImages.setProfileImage(response.imageUrl());
                    newModelProfileImages.setPublicId(response.publicId());
                    newModelProfileImages.setModelProfile(modelProfile);
                    return newModelProfileImages;
                })
                .collect(Collectors.toList());
        this.modelProfileImagesRepository.saveAll(modelProfileImages);
    }

    private void validateModelCreationRequest(ModelCreationRequest modelCreationRequest) {
        if (modelCreationRequest.age() == null) {
            throw new IllegalArgumentException("age is required please add it");
        }
        if (modelCreationRequest.city() == null || modelCreationRequest.city().isBlank()) {
            throw new IllegalArgumentException("city is required please add it");
        }
        if (modelCreationRequest.hairColor() == null || modelCreationRequest.hairColor().isBlank()) {
            throw new IllegalArgumentException("hairColor is required please add it");
        }
        if (modelCreationRequest.heightCm() == null) {
            throw new IllegalArgumentException("height is required please add it");
        }
        if (modelCreationRequest.weightKg() == null) {
            throw new IllegalArgumentException("weight is required please add it");
        }
        if (modelCreationRequest.bio() == null || modelCreationRequest.bio().isBlank()) {
            throw new IllegalArgumentException("bio is required please add it");
        }
        if (modelCreationRequest.bodyType() == null) {
            throw new IllegalArgumentException(
                    "bodyType is required please add it choose between SLIM,ATHLETIC,AVERAGE,CURVY,PLUS_SIZE");
        }
        if (modelCreationRequest.skinTone() == null) {
            throw new IllegalArgumentException(
                    "skinTone is required please add it choose between FAIR,LIGHT,MEDIUM,OLIVE,TAN,DARK");
        }
        if (modelCreationRequest.files() == null || modelCreationRequest.files().isEmpty()) {
            throw new IllegalArgumentException("photos are required please add it");
        }
    }

    private void validateAvailableForRequest(ModelAvailableForRequest request) {
        if (request.availableFor() == null || request.availableFor().isEmpty()) {
            throw new IllegalArgumentException("availableFor is required please add it");
        }

        request.availableFor().forEach(availableFor -> {
            if (availableFor.availableFor() == null) {
                throw new IllegalArgumentException(
                        "availableFor is required please add it one of them PHOTO_SHOOT,FASHION_SHOW,PRODUCT_MODELING,SOCIAL_MEDIA_CONTENT,BRAND_CAMPAIGN,VIDEO_SHOOT");
            }
            if (availableFor.pricePerSession() == null) {
                throw new IllegalArgumentException("pricePerSession is required please add it");
            }
        });
    }
}
