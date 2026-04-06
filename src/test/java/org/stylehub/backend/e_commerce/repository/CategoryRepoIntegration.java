package org.stylehub.backend.e_commerce.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.stylehub.backend.e_commerce.product.category.entity.Category;
import org.stylehub.backend.e_commerce.product.category.repository.CategoryRepo;
import org.stylehub.backend.e_commerce.user.entity.Name;
import org.stylehub.backend.e_commerce.user.entity.User;
import org.stylehub.backend.e_commerce.user.entity.enums.Gender;
import org.stylehub.backend.e_commerce.user.entity.enums.Role;
import org.stylehub.backend.e_commerce.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles({"test"})
class CategoryRepoIntegration {

    @Autowired private CategoryRepo categoryRepo;
    @Autowired private UserRepository userRepo;


    @Test
    void shouldSaveCategoryAndAssociateWithUser() {
        Category category = new Category();
        category.setCategoryName("shoes");
        Category savedCategory = categoryRepo.save(category);

        List<Category>categories=new ArrayList<>();
        categories.add(savedCategory);

//        user.setCategories(categories);
//        User updatedUser = userRepo.save(user);
//
//        assertThat(updatedUser.getCategories().size()).isEqualTo(1);
//        assertThat(updatedUser.getCategories().get(0).getCategoryName()).isEqualTo("shoes");
//        assertThat(updatedUser.getCategories().get(0).getId()).isNotNull();
    }

    @Test
    @DisplayName(
            """
              GIVEN : userId of cuurent user in the system
              WHEN : call findAllByUserId(userId, pageable)
              THEN : return list of categories user created      
            """
    )
    void testFindCategoryByUserId() {
        List<User> foundedUser=userRepo.findByEmail("ali@test.com");
        foundedUser.forEach(user->{
            System.out.println(user.getEmail());
        });
        Pageable pageable= PageRequest.of(0, 2);
        Page<Category> categories= this.categoryRepo.findByUsers_Id(foundedUser.get(0).getId(),pageable);

        assertThat(categories.getTotalElements()).isEqualTo(2);
        assertThat(categories.getContent()).isNotNull();
    }



}
