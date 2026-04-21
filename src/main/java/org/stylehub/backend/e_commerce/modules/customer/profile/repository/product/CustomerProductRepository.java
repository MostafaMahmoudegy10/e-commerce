package org.stylehub.backend.e_commerce.modules.customer.profile.repository.product;

import org.springframework.data.domain.Pageable;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.CustomerShowProductDetailsDto;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.product.FindAllProductFilterRequestDto;

import java.util.Map;
import java.util.UUID;

public interface CustomerProductRepository{

    public Map<String,Object> findAllProductWithFilter(FindAllProductFilterRequestDto dtoRequest, Pageable pageable,String brandId);

    CustomerShowProductDetailsDto showProductDetails(String brandId, UUID productId, UUID itemId);
}
