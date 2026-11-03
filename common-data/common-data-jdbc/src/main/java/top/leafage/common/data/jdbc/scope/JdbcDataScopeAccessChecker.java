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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import top.leafage.common.data.core.scope.DataSubject;
import top.leafage.common.data.core.scope.guard.DataScopeAccessChecker;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * JDBC implementation of single-resource access checks.
 *
 * @author wq li
 */
public final class JdbcDataScopeAccessChecker implements DataScopeAccessChecker {

    private final NamedParameterJdbcOperations jdbcOperations;
    private final MappingTable table;

    /**
     * Creates the checker.
     *
     * @param mapping        the application mapping configuration
     * @param mappingContext the relational mapping context
     * @param jdbcOperations the JDBC operations
     * @param dialect        the active dialect
     */
    public JdbcDataScopeAccessChecker(ResourceSubjectMapping mapping,
                                      RelationalMappingContext mappingContext,
                                      NamedParameterJdbcOperations jdbcOperations,
                                      Dialect dialect) {
        Objects.requireNonNull(mapping, "mapping must not be null");
        Objects.requireNonNull(mappingContext, "mappingContext must not be null");
        this.jdbcOperations = Objects.requireNonNull(jdbcOperations,
                "jdbcOperations must not be null");
        this.table = MappingTable.from(mapping, mappingContext, dialect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccessible(String resource, Object resourceId, Set<DataSubject> owners) {
        if (owners == null || owners.isEmpty()) {
            return false;
        }
        MappingTable.OwnerPredicate ownerPredicate = table.owners(owners);
        String sql = "select count(1) from " + table.table()
                + " where " + table.resourceColumn() + " = :resource"
                + " and " + table.resourceIdColumn() + " = :resourceId"
                + " and (" + ownerPredicate.sql() + ")";
        Map<String, Object> parameters = new LinkedHashMap<>(ownerPredicate.parameters());
        parameters.put("resource", resource);
        parameters.put("resourceId", resourceId);

        Long count = jdbcOperations.queryForObject(sql, parameters, Long.class);
        return count != null && count > 0;
    }
}
