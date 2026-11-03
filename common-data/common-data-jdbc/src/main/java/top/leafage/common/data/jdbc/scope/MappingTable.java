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
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import top.leafage.common.data.core.scope.DataSubject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Resolved table and column names of the application mapping entity.
 *
 * @param table             the mapping table name
 * @param resourceColumn    column holding the resource code
 * @param resourceIdColumn  column holding the resource primary key
 * @param subjectTypeColumn column holding the subject type
 * @param subjectIdColumn   column holding the subject identifier
 * @author wq li
 */
record MappingTable(String table,
                    String resourceColumn,
                    String resourceIdColumn,
                    String subjectTypeColumn,
                    String subjectIdColumn) {

    /**
     * Resolves the physical names from the relational mapping context.
     *
     * @param mapping the application mapping configuration
     * @param context the relational mapping context
     * @param dialect the active dialect
     * @return the resolved names
     */
    static MappingTable from(ResourceSubjectMapping mapping, RelationalMappingContext context,
                             Dialect dialect) {
        RelationalPersistentEntity<?> entity =
                context.getRequiredPersistentEntity(mapping.entityType());
        IdentifierProcessing processing = dialect.getIdentifierProcessing();
        return new MappingTable(
                toSql(entity.getQualifiedTableName(), processing),
                column(entity, mapping.resourceProperty(), processing),
                column(entity, mapping.resourceIdProperty(), processing),
                column(entity, mapping.subjectTypeProperty(), processing),
                column(entity, mapping.subjectIdProperty(), processing));
    }

    private static String column(RelationalPersistentEntity<?> entity, String property,
                                 IdentifierProcessing processing) {
        return toSql(entity.getRequiredPersistentProperty(property).getColumnName(), processing);
    }

    private static String toSql(SqlIdentifier identifier, IdentifierProcessing processing) {
        return identifier.toSql(processing);
    }

    /**
     * Builds a portable predicate matching any of the given owners.
     *
     * @param owners the owners to match
     * @return the SQL fragment and its named parameters
     */
    OwnerPredicate owners(Set<DataSubject> owners) {
        StringBuilder sql = new StringBuilder();
        Map<String, Object> parameters = new LinkedHashMap<>();
        int index = 0;
        for (DataSubject owner : owners) {
            if (!sql.isEmpty()) {
                sql.append(" or ");
            }
            sql.append('(')
                    .append(subjectTypeColumn).append(" = :subjectType").append(index)
                    .append(" and ")
                    .append(subjectIdColumn).append(" = :subjectId").append(index)
                    .append(')');
            parameters.put("subjectType" + index, owner.type());
            parameters.put("subjectId" + index, owner.id());
            index++;
        }
        return new OwnerPredicate(sql.toString(), parameters);
    }

    /**
     * A predicate fragment and its named parameters.
     *
     * @param sql        the SQL fragment, without surrounding parentheses
     * @param parameters the named parameters
     */
    record OwnerPredicate(String sql, Map<String, Object> parameters) {
    }
}
