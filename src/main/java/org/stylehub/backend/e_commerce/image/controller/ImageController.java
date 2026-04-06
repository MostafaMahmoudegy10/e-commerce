package org.stylehub.backend.e_commerce.image.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.image.dto.UploadResponse;
import org.stylehub.backend.e_commerce.image.service.ImageService;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "api/v1/image/upload")
    public UploadResponse uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return this.imageService.uploadImage(file);
    }
    @DeleteMapping(value = "api/v1/image/delete")
    private void deleteImage(@RequestParam("imageId") String imageId) throws IOException {
        this.imageService.deleteImage(imageId);
    }
}
