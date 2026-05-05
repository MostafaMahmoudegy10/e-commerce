package org.stylehub.backend.e_commerce.platform.media.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "api/v1/image/upload")
    public CompletableFuture<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return this.imageService.uploadImageAsync(file);
    }
    @DeleteMapping(value = "api/v1/image/delete")
    private CompletableFuture<Void> deleteImage(@RequestParam("imageId") String imageId) throws IOException {
        return this.imageService.deleteImageAsync(imageId);
    }
}
