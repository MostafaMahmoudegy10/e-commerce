package org.stylehub.backend.e_commerce.modules.dashboard.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationTypeCountRow;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DashboardNotificationRepository extends JpaRepository<DashboardNotification, UUID>, DashboardNotificationQueryRepository {

    Optional<DashboardNotification> findByIdAndRecipientUser_Id(UUID id, UUID userId);

    long countByRecipientUser_Id(UUID userId);

    long countByRecipientUser_IdAndReadAtIsNull(UUID userId);

    long countByRecipientUser_IdAndReadAtIsNotNull(UUID userId);

    @Query("""
            select new org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationTypeCountRow(
                n.type,
                count(n.id)
            )
            from DashboardNotification n
            where n.recipientUser.id = :userId
            group by n.type
            order by count(n.id) desc
            """)
    List<DashboardNotificationTypeCountRow> countByType(@Param("userId") UUID userId);
}
