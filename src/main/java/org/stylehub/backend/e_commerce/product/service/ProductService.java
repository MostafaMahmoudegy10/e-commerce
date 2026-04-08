package org.stylehub.backend.e_commerce.product.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.stylehub.backend.e_commerce.image.dto.UploadResponse;
import org.stylehub.backend.e_commerce.image.entity.ProductItemImage;
import org.stylehub.backend.e_commerce.image.service.ImageService;
import org.stylehub.backend.e_commerce.product.dto.ProductDto;
import org.stylehub.backend.e_commerce.product.dto.ProductDtoRequest;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemDto;
import org.stylehub.backend.e_commerce.product.product_item.entity.ProductItem;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageService imageService;




}
