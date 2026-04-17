package org.stylehub.backend.e_commerce.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.stylehub.backend.e_commerce.order.entity.ReturnRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, UUID> {

    List<ReturnRequest> findAllByUser_ExternalUserIdOrderByCreatedAtDesc(String externalUserId);

    @Query("""
            select rr
            from ReturnRequest rr
            join rr.order o
            join o.brand b
            join b.user u
            where u.externalUserId = :externalUserId
            order by rr.createdAt desc
            """)
    List<ReturnRequest> findAllByBrandOwnerExternalUserId(String externalUserId);

    Optional<ReturnRequest> findByIdAndUser_ExternalUserId(UUID returnRequestId, String externalUserId);

    @Query("""
            select rr
            from ReturnRequest rr
            join rr.order o
            join o.brand b
            join b.user u
            where rr.id = :returnRequestId
              and u.externalUserId = :externalUserId
            """)
    Optional<ReturnRequest> findByIdAndBrandOwnerExternalUserId(UUID returnRequestId, String externalUserId);
}
