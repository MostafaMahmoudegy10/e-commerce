package org.stylehub.backend.e_commerce.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.brand.entity.Brand;

import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
    @Query("""
        select  b from Brand  b
                inner join User  u
            on b.user.id = u.id
        where u.externalUserId=:externalId                                   
        """)
    Optional<Brand> findByUser_ExternalUserId(String externalId);

    @Query("""
             select (count(b)>0)
                         from Brand  b
                         where  b.user.externalUserId=:externalId                        
            """)
   boolean existsByUser_ExternalUserId(String externalId);

}
