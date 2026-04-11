package org.stylehub.backend.e_commerce.modules.catalog.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.FindAllCategoryResponse;
import org.stylehub.backend.e_commerce.modules.catalog.category.repository.CategoryRepository;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class TestFindAllCategories {

    @Autowired
    private CategoryService categoryService;

    @MockitoBean
    private CategoryRepository  categoryRepository;

    @Test
    @DisplayName("""
                Given : size and page
                When : there is not categories in the db
                Then : IllegalArgumentException thrown
                """)
    void testFindAllCategories_CategoriesNotPresentInDb(){
        when(categoryRepository.count())
                .thenReturn(0L);
        assertThrows(IllegalArgumentException.class,()->
                categoryService.findAllCategories(PageRequest.of(0,10)));
    }
    @Test
    @DisplayName("""
                 Given : size and page
                 When : there is categories in db
                 then : pageable categories will return
                  """)
    void testFindAllCategories_CategoriesPresentInDb(){
           when(categoryRepository.count())
                   .thenReturn(1L);
           FindAllCategoryResponse mockResponseV1 =new FindAllCategoryResponse(
                   "BAGS",
                   Gender.MALE,
                   UUID.randomUUID(),
                   null,
                   "no desc"
           );
        FindAllCategoryResponse mockResponseV2 =new FindAllCategoryResponse(
                "BAGS",
                Gender.MALE,
                UUID.randomUUID(),
                null,
                "no desc"
        );
          PageImpl page=new PageImpl(List.of(mockResponseV1,mockResponseV2));
           when(categoryRepository.findAllPageableCategory(PageRequest.of(0, 10)))
                   .thenReturn(page);

           assertThat(page.getTotalElements()).isEqualTo(2);
           assertThat(page.getTotalPages()).isEqualTo(1);

    }

}
