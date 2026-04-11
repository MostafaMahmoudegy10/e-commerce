package org.stylehub.backend.e_commerce.modules.catalog.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import org.stylehub.backend.e_commerce.platform.media.dto.UploadResponse;
import org.stylehub.backend.e_commerce.platform.media.service.ImageService;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.CategoryCreateRequest;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.CategoryResponse;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.modules.catalog.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles({"test"})
public class AddNewCategory {

    @Autowired
    private CategoryService categoryService;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private ImageService imageService;


    @Test
    @DisplayName("""
                GIVEN : Category request dto
                WHEN : CategoryName is empty
                THEN : IlligalArgumentException will thrown 
                """)
    void testAddNewCategoryWithCategoryNameNull() {
        CategoryCreateRequest request = new CategoryCreateRequest(
                "",
                  'M',
                  "new info",
                  null,
                null
        );
        assertThrows(IllegalArgumentException.class,()
        ->categoryService.addNewCategory(request));
    }


    @Test
    @DisplayName("""
                GIVEN : Category request dto
                WHEN : Category already exists
                THEN : IlligalArgumentException will thrown 
                """)
    void testAddNewCategoryWithCategoryNameAlreadyExists() {
        CategoryCreateRequest request = new CategoryCreateRequest(
                "Bags",
                'M',
                "new info",
                null,
                null

        );
        when(this.categoryRepository.existsByCategoryNameIgnoreCase("Bags"))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,()
                ->categoryService.addNewCategory(request));
    }

    @Test
    @DisplayName("""
                GIVEN : Category request dto
                WHEN : Category PARENT  Not Exists
                THEN : IlligalArgumentException will thrown 
                """)
    void testAddNewCategoryWithPatentCategoryNotExists() {
        UUID parentId = UUID.randomUUID();
        CategoryCreateRequest request = new CategoryCreateRequest(
                "Bags",
                'M',
                "new info",
                null,
                parentId
        );

        when(this.categoryRepository.findById(parentId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,()
                ->categoryService.addNewCategory(request));
    }

    @Test
    @DisplayName("""
                    Given : Category request dto
                    When : category have parent category
                    then : category will be saved and be sub category of parent
                """)
    void testAddNewCategoryWithParentCategory(){
        UUID parentId = UUID.randomUUID();

        MultipartFile file=mock(MultipartFile.class);

        CategoryCreateRequest request = new CategoryCreateRequest(
                "Bags",
                'M',
                "new info",
                file,
                parentId
        );

        Category parentCategory =new Category();
        parentCategory.setCategoryName("Clothes");
        parentCategory.setParentCategory(null);
        parentCategory.setCategoryGender(Gender.MALE);
        parentCategory.setId(parentId);

        when(this.categoryRepository.existsByCategoryNameIgnoreCase("Bags"))
                .thenReturn(false);

        when(this.categoryRepository.findById(parentId))
                .thenReturn(Optional.of(parentCategory));

        try {
            when(imageService.uploadImage(file))
                    .thenReturn( new UploadResponse("www","123"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response= this.categoryService.addNewCategory(request);

        assertThat(response.categoryName()).isEqualTo("Bags");
        assertThat(response.categoryGender()).isEqualTo(Gender.MALE);
        assertThat(response.parentCategoryId()).isEqualTo(parentId);
    }

}
