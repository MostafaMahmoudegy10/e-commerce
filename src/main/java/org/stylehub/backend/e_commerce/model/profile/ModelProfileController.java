package org.stylehub.backend.e_commerce.model.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelAvailableForRequest;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelCreationRequest;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelCreationResponse;
import org.stylehub.backend.e_commerce.model.profile.service.ModelService;

@RestController
@RequestMapping(value = "api/v1/model")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class ModelProfileController {

    private final ModelService modelService;

    @PostMapping
    public ResponseEntity<ModelCreationResponse> createModel(@ModelAttribute ModelCreationRequest modelCreationRequest) {
        return ResponseEntity.ok(this.modelService.createModel(modelCreationRequest));
    }

    @PostMapping("/available-for")
    public ResponseEntity<String> addAvailableFor(@RequestBody ModelAvailableForRequest request) {
        this.modelService.addAvailableFor(request);
        return ResponseEntity.ok("availableFor added successfully");
    }
}
