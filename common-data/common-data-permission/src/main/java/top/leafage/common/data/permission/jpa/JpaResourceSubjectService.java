package top.leafage.common.data.permission.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.Table;
import org.springframework.data.util.ProxyUtils;
import org.springframework.util.StringUtils;

/**
 * JPA implementation that derives mapping metadata from entity types and primary keys.
 */
public class JpaResourceSubjectService implements ResourceSubjectService {

    private final EntityManager entityManager;
    private final PersistenceUnitUtil persistenceUnitUtil;

    /**
     * Creates a JPA resource-subject service.
     *
     * @param entityManager transaction-aware entity manager
     */
    public JpaResourceSubjectService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(Object entity) {
        bind(entity, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(Object resource, Object subject) {
        EntityDescriptor resourceDescriptor = descriptor(resource);
        EntityDescriptor subjectDescriptor = descriptor(subject);
        Long resourceId = resourceIdentifier(resource);
        Object subjectId = persistenceUnitUtil.getIdentifier(subject);
        if (subjectId == null) {
            throw new IllegalArgumentException("Subject entity must be persisted before binding");
        }
        entityManager.createQuery("""
                        delete from ResourceSubject mapping
                        where mapping.resource = :resource
                          and mapping.resourceId = :resourceId
                          and mapping.subjectType = :subjectType
                        """)
                .setParameter("resource", resourceDescriptor.tableName())
                .setParameter("resourceId", resourceId)
                .setParameter("subjectType", subjectDescriptor.entityType())
                .executeUpdate();
        entityManager.persist(new ResourceSubject(resourceDescriptor.tableName(), resourceId,
                subjectDescriptor.entityType(), subjectId.toString()));
    }

    private Long resourceIdentifier(Object resource) {
        Object id = persistenceUnitUtil.getIdentifier(resource);
        if (id instanceof Number number) {
            return number.longValue();
        }
        throw new IllegalArgumentException("Resource entity must have a numeric primary key");
    }

    private EntityDescriptor descriptor(Object entity) {
        Class<?> entityType = ProxyUtils.getUserClass(entity);
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Resource subject binding requires a JPA entity");
        }
        Table table = entityType.getAnnotation(Table.class);
        if (table == null || !StringUtils.hasText(table.name())) {
            throw new IllegalArgumentException("Resource subject binding requires @Table(name)");
        }
        return new EntityDescriptor(table.name(), entityType.getName());
    }

    private record EntityDescriptor(String tableName, String entityType) {
    }
}
