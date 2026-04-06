package org.stylehub.backend.e_commerce.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.stylehub.backend.e_commerce.image.dto.UploadResponse;
import org.stylehub.backend.e_commerce.image.service.ImageService;
import org.stylehub.backend.e_commerce.product.dto.ProductDto;
import org.stylehub.backend.e_commerce.product.dto.ProductDtoRequest;
import org.stylehub.backend.e_commerce.product.dto.ProductDtoResponse;
import org.stylehub.backend.e_commerce.product.entity.Product;
import org.stylehub.backend.e_commerce.product.product_item.dto.ProductItemDto;
import org.stylehub.backend.e_commerce.product.product_item.entity.Size;
import org.stylehub.backend.e_commerce.product.repository.ProductRepository;
import org.stylehub.backend.e_commerce.product.service.ProductService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles({"test"})
class AddNewProductIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @MockitoBean
    private ImageService imageService;

    @Test
    @DisplayName("""
            GIVEN: new product with items
            WHEN: product does not exist
            THEN: product should be saved and returned
            """)
    void addNewProductNotExist() throws IOException {
        // GIVEN
        ProductDto productDto = new ProductDto(
                "T-Shirt",
                "Cool T-Shirt",
                null // عشان imageService هيكراش لو مش عامل mock
        );
        ProductItemDto itemDto = new ProductItemDto(
                "red",
                Size.LARGE,
                22,
                BigDecimal.valueOf(100),
                "sku122",
                List.of()
        );
        when(imageService.uploadImage(any()))
                .thenReturn(new UploadResponse("fake-url", "fake-id"));

        ProductDtoRequest request = new ProductDtoRequest(
                productDto,
                List.of(itemDto),
                null
        );
        // WHEN
        ProductDtoResponse response = productService.addNewProduct(request);
        // THEN
        List<Product> products = productRepository.findAll();
        products.forEach(product -> {
            System.out.println(product);
        });
        assert !products.isEmpty();
        Product savedProduct = products.get(0);
        assert savedProduct.getProductName().equals("T-Shirt");
    }
}