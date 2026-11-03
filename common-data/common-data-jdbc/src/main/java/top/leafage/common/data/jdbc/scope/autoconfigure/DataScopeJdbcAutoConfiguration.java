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

package top.leafage.common.data.jdbc.scope.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import top.leafage.common.data.core.scope.DataScopeEngine;
import top.leafage.common.data.core.scope.ResourceSubjectService;
import top.leafage.common.data.core.scope.config.DataScopeAspectConfiguration;
import top.leafage.common.data.core.scope.config.DataScopeCoreConfiguration;
import top.leafage.common.data.core.scope.guard.DataScopeAccessChecker;
import top.leafage.common.data.jdbc.scope.DataScopeJdbcSupport;
import top.leafage.common.data.jdbc.scope.DataScopePredicateResolver;
import top.leafage.common.data.jdbc.scope.JdbcDataScopeAccessChecker;
import top.leafage.common.data.jdbc.scope.JdbcDataScopePredicateResolver;
import top.leafage.common.data.jdbc.scope.JdbcResourceSubjectService;
import top.leafage.common.data.jdbc.scope.ResourceResolver;
import top.leafage.common.data.jdbc.scope.ResourceSubjectMapping;
import top.leafage.common.data.jdbc.scope.TableNameResourceResolver;

/**
 * JDBC adapter auto-configuration.
 *
 * <p>It ships no entity and no fixed table. An application opting in declares a
 * {@link ResourceSubjectMapping} for its own mapping entity; alternatively it
 * provides its own {@link DataScopePredicateResolver}.</p>
 *
 * @author wq li
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "data.scope", name = "enabled", havingValue = "true")
@ConditionalOnClass({NamedParameterJdbcOperations.class, JdbcAggregateOperations.class,
        RelationalMappingContext.class})
@Import({DataScopeCoreConfiguration.class, DataScopeAspectConfiguration.class})
public class DataScopeJdbcAutoConfiguration {

    /**
     * Default resource resolver.
     *
     * @return the resolver
     */
    @Bean
    @ConditionalOnMissingBean
    public ResourceResolver resourceResolver() {
        return new TableNameResourceResolver();
    }

    /**
     * Default predicate resolver, available when a mapping is configured.
     *
     * @param mapping        the application mapping configuration
     * @param mappingContext the relational mapping context
     * @param jdbcOperations the JDBC operations
     * @param dialect        the active dialect
     * @return the predicate resolver
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ResourceSubjectMapping.class)
    public DataScopePredicateResolver dataScopePredicateResolver(
            ResourceSubjectMapping mapping,
            RelationalMappingContext mappingContext,
            NamedParameterJdbcOperations jdbcOperations,
            Dialect dialect) {
        return new JdbcDataScopePredicateResolver(mapping, mappingContext, jdbcOperations, dialect);
    }

    /**
     * Support used by the repository base class.
     *
     * @param engine            the decision engine
     * @param predicateResolver the predicate resolver
     * @param resourceResolver  the resource resolver
     * @return the support component
     */
    @Bean
    @ConditionalOnMissingBean
    public DataScopeJdbcSupport dataScopeJdbcSupport(
            DataScopeEngine engine,
            DataScopePredicateResolver predicateResolver,
            ResourceResolver resourceResolver) {
        return new DataScopeJdbcSupport(engine, predicateResolver, resourceResolver);
    }

    /**
     * Default single-resource access checker.
     *
     * @param mapping        the application mapping configuration
     * @param mappingContext the relational mapping context
     * @param jdbcOperations the JDBC operations
     * @param dialect        the active dialect
     * @return the checker
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ResourceSubjectMapping.class)
    public DataScopeAccessChecker dataScopeAccessChecker(
            ResourceSubjectMapping mapping,
            RelationalMappingContext mappingContext,
            NamedParameterJdbcOperations jdbcOperations,
            Dialect dialect) {
        return new JdbcDataScopeAccessChecker(mapping, mappingContext, jdbcOperations, dialect);
    }

    /**
     * Default resource-subject binding service.
     *
     * @param mapping          the application mapping configuration
     * @param mappingContext   the relational mapping context
     * @param operations       the JDBC aggregate operations
     * @param resourceResolver the resource resolver
     * @param jdbcOperations   the JDBC operations
     * @param dialect          the active dialect
     * @return the service
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ResourceSubjectMapping.class)
    public ResourceSubjectService resourceSubjectService(
            ResourceSubjectMapping mapping,
            RelationalMappingContext mappingContext,
            JdbcAggregateOperations operations,
            ResourceResolver resourceResolver,
            NamedParameterJdbcOperations jdbcOperations,
            Dialect dialect) {
        return new JdbcResourceSubjectService(mapping, mappingContext, operations,
                resourceResolver, jdbcOperations, dialect);
    }
}
