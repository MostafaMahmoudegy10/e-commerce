package org.stylehub.backend.e_commerce.modules.catalog.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.FindAllCategoryResponse;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByCategoryNameIgnoreCase(String categoryName);

    boolean existsByCategoryNameIgnoreCaseAndBrand_Id(String categoryName, UUID brandId);

    @Query(value = """
             select new org.stylehub.backend.e_commerce.modules.catalog.category.dto
                        .FindAllCategoryResponse(c.categoryName,c.categoryGender,c.id
                                   ,c.imageUrl,c.categoryDescription) from Category c
                        where c.brand.id = :brandId
                        order by c.categoryName asc
           """,
            countQuery = "select count(c) from Category c where c.brand.id = :brandId")
    Page<FindAllCategoryResponse> findAllPageableCategoryByBrandId(UUID brandId, Pageable pageable);

    @Query(value = """
             select new org.stylehub.backend.e_commerce.modules.catalog.category.dto
                        .FindAllCategoryResponse(c.categoryName,c.categoryGender,c.id
                                   ,c.imageUrl,c.categoryDescription) from Category c
                        where c.brand is null
                        order by c.categoryName asc
           """,
            countQuery = "select count(c) from Category c where c.brand is null")
    Page<FindAllCategoryResponse> findAllGlobalCategories(Pageable pageable);
    @Deprecated
    @Query(value = """
             select new org.stylehub.backend.e_commerce.modules.catalog.category.dto
                        .FindAllCategoryResponse(c.categoryName,c.categoryGender,c.id
                                   ,c.imageUrl,c.categoryDescription) from Category c
                        order by c.categoryName asc
           """,
            countQuery = "select count(c) from Category c")
    Page<FindAllCategoryResponse> findAllPageableCategory(Pageable pageable);

}
