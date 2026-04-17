package org.stylehub.backend.e_commerce.modules.customer.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.modules.customer.favorite.entity.Favorite;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    boolean existsByUser_ExternalUserIdAndProduct_Id(String externalUserId, UUID productId);

    List<Favorite> findAllByUser_ExternalUserId(String externalUserId);

    Optional<Favorite> findByUser_ExternalUserIdAndProduct_Id(String externalUserId, UUID productId);
}
