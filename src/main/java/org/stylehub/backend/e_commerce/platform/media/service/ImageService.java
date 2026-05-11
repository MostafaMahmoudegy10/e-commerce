package org.stylehub.backend.e_commerce.platform.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);
    private static final String PRODUCTS_FOLDER = "e-commerce/products";

    private final Cloudinary cloudinary;

    @Async("imageTaskExecutor")
    public CompletableFuture<UploadResponse> uploadImageAsync(MultipartFile file) {
        return CompletableFuture.completedFuture(uploadImageInternal(file));
    }

    @Async("imageTaskExecutor")
    public CompletableFuture<UploadResponse> uploadAssetAsync(MultipartFile file, String folder) {
        return CompletableFuture.completedFuture(uploadAssetInternal(file, folder));
    }

    @Async("imageTaskExecutor")
    public CompletableFuture<Void> deleteImageAsync(String publicId) {
        deleteImageInternal(publicId);
        return CompletableFuture.completedFuture(null);
    }

    @Async("imageTaskExecutor")
    public CompletableFuture<Void> deleteImagesAsync(List<String> publicIds) {
        publicIds.stream()
                .filter(publicId -> publicId != null && !publicId.isBlank())
                .forEach(publicId -> {
                    try {
                        deleteImageInternal(publicId);
                    } catch (RuntimeException exception) {
                        LOGGER.error("Failed to delete image from Cloudinary for publicId={}", publicId, exception);
                    }
                });

        return CompletableFuture.completedFuture(null);
    }

    @Async("imageTaskExecutor")
    public CompletableFuture<Void> deleteAssetAsync(String publicId, String resourceType) {
        deleteAssetInternal(publicId, resourceType);
        return CompletableFuture.completedFuture(null);
    }

    private UploadResponse uploadImageInternal(MultipartFile file) {
        return uploadAssetInternal(file, PRODUCTS_FOLDER);
    }

    private UploadResponse uploadAssetInternal(MultipartFile file, String folder) {
        try {
            Map uploadResults = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto"
                    )
            );
            return new UploadResponse(
                    uploadResults.get("secure_url").toString(),
                    uploadResults.get("public_id").toString()
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("the file could not be uploaded" + e.getMessage());
        }
    }

    private void deleteImageInternal(String publicId) {
        deleteAssetInternal(publicId, "image");
    }

    private void deleteAssetInternal(String publicId, String resourceType) {
        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "invalidate", true,
                            "resource_type", resourceType
                    )
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("the file is not presented " + e.getMessage());

        }
    }
}
