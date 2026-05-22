package org.stylehub.backend.e_commerce.model.profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelAvailableForRequest;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelCreationRequest;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelCreationResponse;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelProfileResponse;
import org.stylehub.backend.e_commerce.model.profile.service.ModelService;

@RestController
@RequestMapping(value = "api/v1/model")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Tag(name = "Model Profile", description = "Create and maintain a customer's model profile.")
public class ModelProfileController {

    private final ModelService modelService;

    @GetMapping("/me")
    public ResponseEntity<ModelProfileResponse> getCurrentModelProfile() {
        return ResponseEntity.ok(this.modelService.getCurrentModelProfile());
    }

    @Operation(summary = "Create model profile", description = "Creates a model profile for the authenticated customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Model profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid model profile data or business validation error"),
            @ApiResponse(responseCode = "401", description = "JWT token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not allowed to create a model profile")
    })
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
