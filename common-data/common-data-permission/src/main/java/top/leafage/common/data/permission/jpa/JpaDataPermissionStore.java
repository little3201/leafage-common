package top.leafage.common.data.permission.jpa;

import jakarta.persistence.EntityManager;

import java.util.Collection;
import java.util.List;

/**
 * JPA implementation of generic data permission policy access.
 */
public class JpaDataPermissionStore implements DataPermissionStore {

    private final EntityManager entityManager;

    /**
     * Creates a JPA data permission store.
     *
     * @param entityManager transaction-aware entity manager
     */
    public JpaDataPermissionStore(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataPermission> findByResourceAndSubjectTypeAndSubjectIds(String resource, String subjectType,
                                                                            Collection<String> subjectIds) {
        if (subjectIds.isEmpty()) {
            return List.of();
        }
        return entityManager.createQuery("""
                        select p from DataPermission p
                        where p.resource = :resource
                          and p.subjectType = :subjectType
                          and p.subjectId in :subjectIds
                        """, DataPermission.class)
                .setParameter("resource", resource)
                .setParameter("subjectType", subjectType)
                .setParameter("subjectIds", subjectIds)
                .getResultList();
    }
}
