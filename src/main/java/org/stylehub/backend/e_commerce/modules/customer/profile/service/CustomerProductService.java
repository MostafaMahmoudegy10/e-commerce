package org.stylehub.backend.e_commerce.modules.customer.profile.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.FindAllProductFilterRequestDto;
import org.stylehub.backend.e_commerce.modules.customer.profile.repository.product.CustomerProductRepository;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerProductService {

    private final CustomerProductRepository productRepository;

    public Map<String,Object> findAllProductWithFilter(FindAllProductFilterRequestDto dtoRequest, Pageable pageable) {

        return this. productRepository.findAllProductWithFilter(dtoRequest,pageable);
    }
}
