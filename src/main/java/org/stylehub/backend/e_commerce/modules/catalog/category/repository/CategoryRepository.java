package org.stylehub.backend.e_commerce.modules.catalog.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.FindAllCategoryResponse;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category,UUID> {


    // Find if a categoryName is persent or not
    boolean existsByCategoryNameEnAndBrand_Id(@Param("categoryEn")
              String categoryEn, @Param("brandId")UUID brandId);

    // this query will return the categories of a brand
    @Query("""
          select c.id,c.categoryNameEn,c.categoryNameAr,
                    c.categoryDescriptionEn,
                    c.categoryDescriptionAr,c.imageUrl    
               from Category c
                     where c.brand.id=:userId                           
           """)
    Page<Map<String,Object>> findAllByBrand_Id(@Param("userId") UUID brandId, Pageable pageable);


    @Query("""
           select c from Category c 
                      where c.id=:categoryId and c.brand.id=:brandId
                      """)
    Optional<Category> findByIdAndBrand_Id(@Param("categoryId") UUID categoryId,
                                        @Param("brandId")   UUID brandId);
}
