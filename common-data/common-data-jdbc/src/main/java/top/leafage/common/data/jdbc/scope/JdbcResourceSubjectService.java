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

package top.leafage.common.data.jdbc.scope;

import org.springframework.beans.BeanUtils;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import top.leafage.common.data.core.scope.DataSubject;
import top.leafage.common.data.core.scope.ResourceSubjectService;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Generic JDBC resource-subject mapping service.
 *
 * <p>The mapping entity belongs to the application. It must have a no-argument
 * constructor and fields matching the configured property names. Methods must
 * run inside the caller's transaction so a mapping cannot be committed
 * independently of the resource it describes.</p>
 *
 * @author wq li
 */
@Transactional(propagation = Propagation.MANDATORY)
public class JdbcResourceSubjectService implements ResourceSubjectService {

    private final RelationalMappingContext mappingContext;
    private final JdbcAggregateOperations operations;
    private final ResourceSubjectMapping mapping;
    private final ResourceResolver resourceResolver;
    private final NamedParameterJdbcOperations jdbcOperations;
    private final MappingTable table;

    /**
     * Creates the service.
     *
     * @param mapping        the application mapping configuration
     * @param mappingContext the relational mapping context
     * @param operations     the JDBC aggregate operations
     * @param resourceResolver maps entities to resource codes
     * @param jdbcOperations the JDBC operations
     * @param dialect        the active dialect
     */
    public JdbcResourceSubjectService(ResourceSubjectMapping mapping,
                                      RelationalMappingContext mappingContext,
                                      JdbcAggregateOperations operations,
                                      ResourceResolver resourceResolver,
                                      NamedParameterJdbcOperations jdbcOperations,
                                      Dialect dialect) {
        Objects.requireNonNull(mapping, "mapping must not be null");
        this.mappingContext = Objects.requireNonNull(mappingContext,
                "mappingContext must not be null");
        this.operations = Objects.requireNonNull(operations, "operations must not be null");
        this.mapping = mapping;
        this.resourceResolver = Objects.requireNonNull(resourceResolver,
                "resourceResolver must not be null");
        this.jdbcOperations = Objects.requireNonNull(jdbcOperations,
                "jdbcOperations must not be null");
        this.table = MappingTable.from(mapping, mappingContext, dialect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(Object resource, DataSubject subject) {
        Objects.requireNonNull(resource, "resource must not be null");
        Objects.requireNonNull(subject, "subject must not be null");
        Class<?> resourceType = ClassUtils.getUserClass(resource);
        Object resourceId = identifier(resource, resourceType);
        String resourceCode = resourceResolver.resolve(resourceType);
        delete(resourceCode, resourceId, subject);

        Object entity = BeanUtils.instantiateClass(mapping.entityType());
        setField(entity, mapping.resourceProperty(), resourceCode);
        setField(entity, mapping.resourceIdProperty(), resourceId);
        setField(entity, mapping.subjectTypeProperty(), subject.type());
        setField(entity, mapping.subjectIdProperty(), subject.id());
        operations.insert(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbind(Object resource, DataSubject subject) {
        Objects.requireNonNull(resource, "resource must not be null");
        Objects.requireNonNull(subject, "subject must not be null");
        Class<?> resourceType = ClassUtils.getUserClass(resource);
        delete(resourceResolver.resolve(resourceType), identifier(resource, resourceType), subject);
    }

    private Object identifier(Object resource, Class<?> resourceType) {
        PersistentEntity<?, ?> entity = mappingContext.getRequiredPersistentEntity(resourceType);
        Object identifier = entity.getIdentifierAccessor(resource).getIdentifier();
        if (identifier == null) {
            throw new IllegalArgumentException("Resource entity must be persisted before binding");
        }
        return identifier;
    }

    private void delete(String resourceCode, Object resourceId, DataSubject subject) {
        String sql = "delete from " + table.table()
                + " where " + table.resourceColumn() + " = :resource"
                + " and " + table.resourceIdColumn() + " = :resourceId"
                + " and " + table.subjectTypeColumn() + " = :subjectType"
                + " and " + table.subjectIdColumn() + " = :subjectId";
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("resource", resourceCode);
        parameters.put("resourceId", resourceId);
        parameters.put("subjectType", subject.type());
        parameters.put("subjectId", subject.id());
        jdbcOperations.update(sql, parameters);
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
