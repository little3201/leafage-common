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

package top.leafage.common.data.core.scope.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.leafage.common.data.core.scope.DataScopeEngine;
import top.leafage.common.data.core.scope.DataScopePolicyProvider;
import top.leafage.common.data.core.scope.DataSubjectResolver;
import top.leafage.common.data.core.scope.context.DataScopeContextAspect;
import top.leafage.common.data.core.scope.guard.DataScopeAccessChecker;
import top.leafage.common.data.core.scope.guard.DataScopeCheckAspect;
import top.leafage.common.data.core.scope.guard.DataScopeGuard;

/**
 * Core auto-configuration for data scope.
 *
 * <p>Activated explicitly with {@code leafage.data-scope.enabled=true}.
 * The application must provide a {@link DataScopePolicyProvider} and a
 * {@link DataSubjectResolver}; both are business specific.</p>
 *
 * @author wq li
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "leafage.data-scope", name = "enabled", havingValue = "true")
public class DataScopeAutoConfiguration {

    /**
     * Creates the decision engine.
     *
     * @param policyProvider  loads grants
     * @param subjectResolver resolves owners for limited scopes
     * @return the engine
     */
    @Bean
    @ConditionalOnMissingBean
    public DataScopeEngine dataScopeEngine(DataScopePolicyProvider policyProvider,
                                                DataSubjectResolver subjectResolver) {
        return new DataScopeEngine(policyProvider, subjectResolver);
    }

    /**
     * Creates the single-resource guard.
     *
     * @param engine        the decision engine
     * @param accessChecker optional single-resource checker
     * @return the guard
     */
    @Bean
    @ConditionalOnMissingBean
    public DataScopeGuard dataScopeGuard(DataScopeEngine engine,
                                              ObjectProvider<DataScopeAccessChecker> accessChecker) {
        return new DataScopeGuard(engine, accessChecker);
    }

    /**
     * AOP support, only loaded when AspectJ is present.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    static class AspectConfiguration {

        @Bean
        @ConditionalOnMissingBean
        DataScopeContextAspect dataScopeContextAspect() {
            return new DataScopeContextAspect();
        }

        @Bean
        @ConditionalOnMissingBean
        DataScopeCheckAspect dataScopeCheckAspect(DataScopeGuard guard) {
            return new DataScopeCheckAspect(guard);
        }
    }
}
