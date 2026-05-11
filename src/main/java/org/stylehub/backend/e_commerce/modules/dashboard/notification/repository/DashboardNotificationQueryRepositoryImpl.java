package org.stylehub.backend.e_commerce.modules.dashboard.notification.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.dto.DashboardNotificationFilterRequest;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotification;
import org.stylehub.backend.e_commerce.modules.dashboard.notification.entity.DashboardNotificationReadFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DashboardNotificationQueryRepositoryImpl implements DashboardNotificationQueryRepository {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Page<DashboardNotification> findNotifications(UUID userId, DashboardNotificationFilterRequest filter, Pageable pageable) {
        try (EntityManager entityManager = this.entityManagerFactory.createEntityManager()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<DashboardNotification> query = cb.createQuery(DashboardNotification.class);
            Root<DashboardNotification> root = query.from(DashboardNotification.class);

            query.select(root);
            query.where(buildPredicates(cb, root, userId, filter));
            query.orderBy(cb.desc(root.get("createdAt")));

            List<DashboardNotification> items = entityManager.createQuery(query)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<DashboardNotification> countRoot = countQuery.from(DashboardNotification.class);
            countQuery.select(cb.count(countRoot));
            countQuery.where(buildPredicates(cb, countRoot, userId, filter));

            long total = entityManager.createQuery(countQuery).getSingleResult();

            return new PageImpl<>(items, pageable, total);
        }
    }

    private Predicate[] buildPredicates(
            CriteriaBuilder cb,
            Root<DashboardNotification> root,
            UUID userId,
            DashboardNotificationFilterRequest filter
    ) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("recipientUser").get("id"), userId));

        if (filter != null && filter.status() != null && filter.status() != DashboardNotificationReadFilter.ALL) {
            if (filter.status() == DashboardNotificationReadFilter.UNREAD) {
                predicates.add(cb.isNull(root.get("readAt")));
            } else if (filter.status() == DashboardNotificationReadFilter.READ) {
                predicates.add(cb.isNotNull(root.get("readAt")));
            }
        }

        if (filter != null && filter.type() != null) {
            predicates.add(cb.equal(root.get("type"), filter.type()));
        }

        if (filter != null && filter.search() != null && !filter.search().isBlank()) {
            String pattern = "%" + filter.search().trim().toLowerCase() + "%";
            Expression<String> title = cb.lower(root.get("title"));
            Expression<String> message = cb.lower(root.get("message"));
            Expression<String> referenceCode = cb.lower(cb.coalesce(root.get("referenceCode"), ""));

            predicates.add(cb.or(
                    cb.like(title, pattern),
                    cb.like(message, pattern),
                    cb.like(referenceCode, pattern)
            ));
        }

        return predicates.toArray(Predicate[]::new);
    }
}
