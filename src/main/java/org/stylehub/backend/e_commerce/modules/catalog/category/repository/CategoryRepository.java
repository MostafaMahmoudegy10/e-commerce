package org.stylehub.backend.e_commerce.modules.catalog.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.FindAllCategoryResponse;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category,UUID> {

    boolean existsByCategoryNameIgnoreCase(String categoryName);

    @Query(value = """
             select new org.stylehub.backend.e_commerce.modules.catalog.category.dto
                        .FindAllCategoryResponse(c.categoryName,c.categoryGender,c.id
                                   ,c.imageUrl,c.categoryDescription) from Category c
                        order by c.categoryName desc 
           """,
    countQuery = "select count (c) from Category c")
    Page<FindAllCategoryResponse> findAllPageableCategory(Pageable pageable);


}
