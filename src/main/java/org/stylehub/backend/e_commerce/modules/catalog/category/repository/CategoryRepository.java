package org.stylehub.backend.e_commerce.modules.catalog.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.FindAllCategoryResponse;
import org.stylehub.backend.e_commerce.modules.catalog.category.dto.findAllByBrandId;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category,UUID> {


    // Find if a categoryName is persent or not
    boolean existsByCategoryNameEnAndBrand_UserExternalUserId(@Param("categoryEn")
              String categoryEn, @Param("externalUserId")String externalUserId);

    // this query will return the categories of a brand
    @Query("""
            select new org.stylehub.backend.e_commerce.modules.catalog.category.dto.findAllByBrandId(
                 c.id,
                 c.categoryNameEn,
                 c.categoryNameAr,
                 c.categoryDescriptionEn,
                 c.categoryDescriptionAr,
                 c.imageUrl
           )
           from Category c
           where c.brand.user.externalUserId = :externalId            
           """)
    Page<findAllByBrandId> findAllByBrandExternalUserId(@Param("externalId") String externalId, Pageable pageable);


    @Query("""
                 select c from Category c
           where c.id = :categoryId and c.brand.user.externalUserId = :externalUserId
           """)
    Optional<Category> findCategoryByIdAndBrand_User_ExternalUserId(
            @Param("categoryId") UUID categoryId,
            @Param("externalUserId") String externalId
    );



}
