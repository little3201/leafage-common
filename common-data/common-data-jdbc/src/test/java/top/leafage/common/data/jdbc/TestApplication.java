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

package top.leafage.common.data.jdbc;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import top.leafage.common.data.core.scope.DataSubject;
import top.leafage.common.data.core.scope.DataSubjectResolver;
import top.leafage.common.data.jdbc.scope.DataScopeJdbcRepository;
import top.leafage.common.data.jdbc.scope.DataScopeJdbcRepositoryFactoryBean;
import top.leafage.common.data.jdbc.scope.ResourceSubjectMapping;

import java.util.Set;

/**
 * Test application wiring the core and JDBC adapter.
 *
 * @author wq li
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJdbcRepositories(basePackageClasses = PostRepository.class,
        repositoryBaseClass = DataScopeJdbcRepository.class,
        repositoryFactoryBeanClass = DataScopeJdbcRepositoryFactoryBean.class)
public class TestApplication {

    /**
     * Test policy provider.
     *
     * @return the provider
     */
    @Bean
    public TestPolicyProvider policyProvider() {
        return new TestPolicyProvider();
    }

    /**
     * Test subject resolver.
     *
     * @return the resolver
     */
    @Bean
    public DataSubjectResolver subjectResolver() {
        return scope -> switch (scope) {
            case SELF -> Set.of(new DataSubject("USER", "alice"));
            case GROUP -> Set.of(new DataSubject("GROUP", "g1"));
            default -> Set.of();
        };
    }

    /**
     * Application-owned mapping configuration.
     *
     * @return the mapping
     */
    @Bean
    public ResourceSubjectMapping resourceSubjectMapping() {
        return ResourceSubjectMapping.of(ResourceSubjectRow.class);
    }
}
