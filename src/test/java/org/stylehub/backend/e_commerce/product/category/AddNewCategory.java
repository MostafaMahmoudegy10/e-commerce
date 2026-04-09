package org.stylehub.backend.e_commerce.product.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.stylehub.backend.e_commerce.product.category.dto.CategoryCreateRequest;
import org.stylehub.backend.e_commerce.product.category.dto.CategoryResponse;
import org.stylehub.backend.e_commerce.product.category.entity.Category;
import org.stylehub.backend.e_commerce.product.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles({"test"})
public class AddNewCategory {

    @Autowired
    private CategoryService categoryService;

    @MockitoBean
    private CategoryRepository categoryRepository;


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
        CategoryCreateRequest request = new CategoryCreateRequest(
                "Bags",
                'M',
                "new info",
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

        Category childCategory =new Category();
        childCategory.setCategoryName(request.categoryName());
        childCategory.setParentCategory(parentCategory);
        childCategory.setCategoryGender(Gender.fromCode(request.categoryGender()));

        when(this.categoryRepository.save(childCategory))
                .thenReturn(childCategory);

        CategoryResponse response= this.categoryService.addNewCategory(request);

        assertThat(response.categoryName()).isEqualTo(childCategory.getCategoryName());
        assertThat(response.categoryGender()).isEqualTo(childCategory.getCategoryGender());
        assertThat(response.parentCategoryId()).isEqualTo(childCategory.getParentCategory().getId());
    }

}
