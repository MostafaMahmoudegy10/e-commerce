package org.stylehub.backend.e_commerce.modules.customer.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stylehub.backend.e_commerce.modules.customer.history.entity.RecentlyViewedProduct;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecentlyViewedProductRepository extends JpaRepository<RecentlyViewedProduct, UUID> {

    Optional<RecentlyViewedProduct> findByUser_ExternalUserIdAndProduct_Id(String externalUserId, UUID productId);

    List<RecentlyViewedProduct> findTop10ByUser_ExternalUserIdOrderByViewedAtDesc(String externalUserId);
}
