package org.stylehub.backend.e_commerce.model.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.model.profile.entity.ModelProfile;
import org.stylehub.backend.e_commerce.model.profile.repository.ModelProfileRepository;
import org.stylehub.backend.e_commerce.platform.security.current_user.CurrentUserProvider;

@Service
@RequiredArgsConstructor
public class ModelProfileAccessService {

    private final ModelProfileRepository modelProfileRepository;
    private final CurrentUserProvider currentUserProvider;

    public ModelProfile requireCurrentModelProfile() {
        return this.modelProfileRepository.findModelProfileByUser_Id(this.currentUserProvider.getUserId())
                .orElseThrow(() -> new IllegalStateException("You need to create a model profile first"));
    }
}
