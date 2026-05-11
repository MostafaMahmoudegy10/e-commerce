package org.stylehub.backend.e_commerce.model.profile.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelSearchFilterRequest;
import org.stylehub.backend.e_commerce.model.profile.dto.ModelSearchRow;

public interface ModelSearchRepository {

    Page<ModelSearchRow> searchModels(ModelSearchFilterRequest filter, Pageable pageable);
}
