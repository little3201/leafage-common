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

import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import top.leafage.common.data.core.scope.DataSubject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Builds an {@code id in (...)} criterion from the application mapping table.
 *
 * <p>The generated condition is equivalent to:</p>
 * <pre>
 * id in (select resourceId from mapping
 *        where resource = :resource
 *          and ((subjectType, subjectId) in (...)))
 * </pre>
 *
 * <p>Spring Data relational criteria cannot express a correlated subquery
 * portably, so the identifiers are resolved first. Applications that need a
 * single statement can replace this bean with their own resolver.</p>
 *
 * @author wq li
 */
public class JdbcDataScopePredicateResolver implements DataScopePredicateResolver {

    private final RelationalMappingContext mappingContext;
    private final NamedParameterJdbcOperations jdbcOperations;
    private final MappingTable table;

    /**
     * Creates the resolver.
     *
     * @param mapping       the application mapping configuration
     * @param mappingContext the relational mapping context
     * @param jdbcOperations the JDBC operations
     * @param dialect       the active dialect
     */
    public JdbcDataScopePredicateResolver(ResourceSubjectMapping mapping,
                                          RelationalMappingContext mappingContext,
                                          NamedParameterJdbcOperations jdbcOperations,
                                          Dialect dialect) {
        Objects.requireNonNull(mapping, "mapping must not be null");
        this.mappingContext = Objects.requireNonNull(mappingContext,
                "mappingContext must not be null");
        this.jdbcOperations = Objects.requireNonNull(jdbcOperations,
                "jdbcOperations must not be null");
        this.table = MappingTable.from(mapping, mappingContext, dialect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Criteria resolve(Class<T> entityType, String resource, Set<DataSubject> owners) {
        if (owners == null || owners.isEmpty()) {
            return deny(entityType);
        }
        MappingTable.OwnerPredicate ownerPredicate = table.owners(owners);
        String sql = "select distinct " + table.resourceIdColumn()
                + " from " + table.table()
                + " where " + table.resourceColumn() + " = :resource"
                + " and (" + ownerPredicate.sql() + ")";
        Map<String, Object> parameters = new LinkedHashMap<>(ownerPredicate.parameters());
        parameters.put("resource", resource);

        List<Object> resourceIds = jdbcOperations.queryForList(sql, parameters, Object.class);
        if (resourceIds.isEmpty()) {
            return deny(entityType);
        }
        return Criteria.where(idProperty(entityType)).in(resourceIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Criteria deny(Class<T> entityType) {
        return Criteria.where(idProperty(entityType)).isNull();
    }

    private String idProperty(Class<?> entityType) {
        RelationalPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(entityType);
        RelationalPersistentProperty id = entity.getRequiredIdProperty();
        if (id.isEmbedded()) {
            throw new UnsupportedOperationException(
                    "Data scope does not support composite identifiers: " + entityType.getName());
        }
        return id.getName();
    }
}
