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

package top.leafage.common.data.core.scope.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import top.leafage.common.data.core.scope.DataScopeEngine;
import top.leafage.common.data.core.scope.DataScopePolicyProvider;
import top.leafage.common.data.core.scope.DataSubjectResolver;
import top.leafage.common.data.core.scope.guard.DataScopeAccessChecker;
import top.leafage.common.data.core.scope.guard.DataScopeGuard;

/**
 * Shared data-scope beans.
 *
 * <p>This is a plain configuration, not an auto-configuration: the core module is
 * never used on its own. Persistence adapters such as the JPA, JDBC and R2DBC
 * modules import it from their own auto-configuration, so service activation
 * (for example {@code data.scope.enabled}) stays in the adapter.</p>
 *
 * @author wq li
 */
@Configuration(proxyBeanMethods = false)
public class DataScopeCoreConfiguration {

    /**
     * Creates the decision engine.
     *
     * @param policyProvider  loads grants
     * @param subjectResolver resolves owners for limited scopes
     * @return the engine
     */
    @Bean
    @Conditional(MissingBeanCondition.class)
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
    @Conditional(MissingBeanCondition.class)
    public DataScopeGuard dataScopeGuard(DataScopeEngine engine,
                                         ObjectProvider<DataScopeAccessChecker> accessChecker) {
        return new DataScopeGuard(engine, accessChecker);
    }
}
