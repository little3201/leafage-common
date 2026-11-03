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

package top.leafage.common.data.jpa.scope.autoconfigure;

import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import top.leafage.common.data.core.scope.DataScopeEngine;
import top.leafage.common.data.core.scope.ResourceSubjectService;
import top.leafage.common.data.core.scope.config.DataScopeAspectConfiguration;
import top.leafage.common.data.core.scope.config.DataScopeCoreConfiguration;
import top.leafage.common.data.core.scope.guard.DataScopeAccessChecker;
import top.leafage.common.data.jpa.scope.*;

/**
 * JPA adapter auto-configuration.
 *
 * <p>It ships no entity and no fixed table. An application opting in declares a
 * {@link ResourceSubjectMapping} for its own mapping entity; alternatively it
 * provides its own {@link DataScopePredicateResolver}.</p>
 *
 * @author wq li
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "data.scope", name = "enabled", havingValue = "true")
@ConditionalOnClass({EntityManager.class, JpaRepository.class})
@Import({DataScopeCoreConfiguration.class, DataScopeAspectConfiguration.class})
public class DataScopeJpaAutoConfiguration {

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
     * @param mapping the application mapping configuration
     * @return the predicate resolver
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ResourceSubjectMapping.class)
    public DataScopePredicateResolver dataScopePredicateResolver(
            ResourceSubjectMapping mapping) {
        return new JpaDataScopePredicateResolver(mapping);
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
    public DataScopeJpaSupport dataScopeJpaSupport(
            DataScopeEngine engine,
            DataScopePredicateResolver predicateResolver,
            ResourceResolver resourceResolver) {
        return new DataScopeJpaSupport(engine, predicateResolver, resourceResolver);
    }

    /**
     * Default single-resource access checker.
     *
     * @param entityManager the entity manager
     * @param mapping       the application mapping configuration
     * @return the checker
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ResourceSubjectMapping.class)
    public DataScopeAccessChecker dataScopeAccessChecker(EntityManager entityManager,
                                                         ResourceSubjectMapping mapping) {
        return new JpaDataScopeAccessChecker(entityManager, mapping);
    }

    /**
     * Default resource-subject binding service.
     *
     * @param entityManager    the entity manager
     * @param mapping          the application mapping configuration
     * @param resourceResolver the resource resolver
     * @return the service
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ResourceSubjectMapping.class)
    public ResourceSubjectService resourceSubjectService(EntityManager entityManager,
                                                         ResourceSubjectMapping mapping,
                                                         ResourceResolver resourceResolver) {
        return new JpaResourceSubjectService(entityManager, mapping, resourceResolver);
    }
}
