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

import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * Creates {@link DataScopeJdbcRepository} instances with injected support.
 *
 * @author wq li
 */
public class DataScopeJdbcRepositoryFactory extends JdbcRepositoryFactory {

    private final JdbcAggregateOperations operations;
    private final DataScopeJdbcSupport support;

    /**
     * Creates the factory.
     *
     * @param operations the JDBC aggregate operations
     * @param support    data scope support
     */
    public DataScopeJdbcRepositoryFactory(JdbcAggregateOperations operations,
                                          DataScopeJdbcSupport support) {
        super(operations);
        this.operations = operations;
        this.support = support;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return DataScopeJdbcRepository.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Object getTargetRepository(RepositoryInformation information) {
        RelationalPersistentEntity<?> entity = operations.getConverter().getMappingContext()
                .getRequiredPersistentEntity(information.getDomainType());
        return new DataScopeJdbcRepository(operations, entity, operations.getConverter(), support);
    }
}
