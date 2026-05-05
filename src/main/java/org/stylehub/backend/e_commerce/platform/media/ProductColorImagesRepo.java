package org.stylehub.backend.e_commerce.platform.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.platform.media.entity.ProductColorImages;

import java.util.List;
import java.util.UUID;

public interface ProductColorImagesRepo extends JpaRepository<ProductColorImages, UUID> {

    List<ProductColorImages> findAllByProductColor_Id(UUID productColorId);

    void deleteAllByProductColor_Id(UUID productColorId);

    @Query("""
        select pc.imageUrl from 
           ProductColorImages pc
           Join pc.productColor c 
            where c.id=:colorId           
                        """)
    List<String> findImageUrlsByProductColor_Id(UUID colorId);
}
