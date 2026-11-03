/*
 * Copyright(c) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.leafage.common.data.jpa.scope;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import top.leafage.common.data.core.scope.DataSubject;
import top.leafage.common.data.core.scope.ResourceSubjectService;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Generic JPA resource-subject mapping service.
 *
 * <p>The mapping entity belongs to the application. It must have a no-argument
 * constructor and fields matching the configured property names. Methods must
 * run inside the caller's transaction so a mapping cannot be committed
 * independently of the resource it describes.</p>
 *
 * @author wq li
 */
@Transactional(propagation = Propagation.MANDATORY)
public class JpaResourceSubjectService implements ResourceSubjectService {

    private final EntityManager entityManager;
    private final ResourceSubjectMapping mapping;
    private final ResourceResolver resourceResolver;

    /**
     * Creates the service.
     *
     * @param entityManager    the entity manager
     * @param mapping          the application mapping configuration
     * @param resourceResolver maps entities to resource codes
     */
    public JpaResourceSubjectService(EntityManager entityManager, ResourceSubjectMapping mapping,
                                     ResourceResolver resourceResolver) {
        this.entityManager = Objects.requireNonNull(entityManager, "entityManager must not be null");
        this.mapping = Objects.requireNonNull(mapping, "mapping must not be null");
        this.resourceResolver = Objects.requireNonNull(resourceResolver,
                "resourceResolver must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(Object resource, DataSubject subject) {
        Objects.requireNonNull(resource, "resource must not be null");
        Objects.requireNonNull(subject, "subject must not be null");
        Object resourceId = entityManager.getEntityManagerFactory().getPersistenceUnitUtil()
                .getIdentifier(resource);
        if (resourceId == null) {
            throw new IllegalArgumentException("Resource entity must be persisted before binding");
        }
        String resourceCode = resourceResolver.resolve(ClassUtils.getUserClass(resource));
        delete(resourceCode, resourceId, subject);

        Object entity = BeanUtils.instantiateClass(mapping.entityType());
        setField(entity, mapping.resourceProperty(), resourceCode);
        setField(entity, mapping.resourceIdProperty(), resourceId);
        setField(entity, mapping.subjectTypeProperty(), subject.type());
        setField(entity, mapping.subjectIdProperty(), subject.id());
        entityManager.persist(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbind(Object resource, DataSubject subject) {
        Objects.requireNonNull(resource, "resource must not be null");
        Objects.requireNonNull(subject, "subject must not be null");
        Object resourceId = entityManager.getEntityManagerFactory().getPersistenceUnitUtil()
                .getIdentifier(resource);
        if (resourceId == null) {
            throw new IllegalArgumentException("Resource entity must be persisted before unbinding");
        }
        delete(resourceResolver.resolve(ClassUtils.getUserClass(resource)), resourceId, subject);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void delete(String resourceCode, Object resourceId, DataSubject subject) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Class entityType = mapping.entityType();
        CriteriaDelete delete = criteriaBuilder.createCriteriaDelete(entityType);
        Root root = delete.from(entityType);
        delete.where(criteriaBuilder.equal(root.get(mapping.resourceProperty()), resourceCode),
                criteriaBuilder.equal(root.get(mapping.resourceIdProperty()), resourceId),
                criteriaBuilder.equal(root.get(mapping.subjectTypeProperty()), subject.type()),
                criteriaBuilder.equal(root.get(mapping.subjectIdProperty()), subject.id()));
        entityManager.createQuery(delete).executeUpdate();
    }

    private void setField(Object target, String name, Object value) {
        Field field = ReflectionUtils.findField(target.getClass(), name);
        if (field == null) {
            throw new IllegalStateException("Mapping property not found: " + name);
        }
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, target, value);
    }
}
