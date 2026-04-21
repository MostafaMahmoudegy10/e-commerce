package org.stylehub.backend.e_commerce.modules.customer.profile.repository.category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.stylehub.backend.e_commerce.modules.catalog.category.entity.Category;
import org.stylehub.backend.e_commerce.modules.customer.profile.dto.category.CategoryNameDto;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomerCategoryRepoImpl implements CustomerCategoryRepo {

    private  final EntityManagerFactory emf;

    private final Logger log = LoggerFactory.getLogger(CustomerCategoryRepoImpl.class);

    @Override
    public List<CategoryNameDto> findAllParentChildCategories(String brandId, String parentCategoryName) {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<CategoryNameDto> cq = cb.createQuery(CategoryNameDto.class);
            Root<Category> categoryRoot = cq.from(Category.class);

            Predicate predicate =cb.conjunction();
            predicate=cb.and(predicate,
                    cb.equal(categoryRoot.get("brand")
                            .get("user")
                            .get("externalUserId"),brandId));

            if(parentCategoryName!=null){
                Join<Category,Category> parentCategoryRoot = categoryRoot.join("parentCategory",JoinType.INNER);
                predicate= cb.and(predicate,
                        cb.equal(parentCategoryRoot.get("categoryNameEn"), parentCategoryName)
                );
            }else{
                predicate= cb.and(predicate,
                        cb.isNull(categoryRoot.get("parentCategory")));
            }
            Subquery<Long> childrenSubquery = cq.subquery(Long.class);
            Root<Category> childRoot = childrenSubquery.from(Category.class);

            childrenSubquery.select(cb.count(childRoot));
            childrenSubquery.where(
                    cb.equal(childRoot.get("parentCategory"), categoryRoot)
            );

            Expression<Boolean> hasChildren = cb.greaterThan(childrenSubquery, 0L);

            cq.select(
                    cb.construct(
                            CategoryNameDto.class,
                            categoryRoot.get("categoryNameEn"),
                            categoryRoot.get("categoryNameAr"),
                            hasChildren
                    )
            ).where(predicate);

            TypedQuery<CategoryNameDto> query = em.createQuery(cq);
            List<CategoryNameDto> result = query.getResultList();
            em.getTransaction().commit();
            log.info("result={}",result);
            return result;
        }
    }
}
