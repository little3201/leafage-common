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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import top.leafage.common.data.core.scope.context.DataScopeContextAspect;
import top.leafage.common.data.core.scope.guard.DataScopeCheckAspect;
import top.leafage.common.data.core.scope.guard.DataScopeGuard;

/**
 * Shared AOP beans.
 *
 * <p>This configuration is imported by an adapter only when AspectJ is on the
 * classpath, so it does not need any class-presence condition itself.</p>
 *
 * @author wq li
 */
@Configuration(proxyBeanMethods = false)
public class DataScopeAspectConfiguration {

    /**
     * Creates the context aspect.
     *
     * @return the aspect
     */
    @Bean
    @Conditional(MissingBeanCondition.class)
    public DataScopeContextAspect dataScopeContextAspect() {
        return new DataScopeContextAspect();
    }

    /**
     * Creates the single-resource check aspect.
     *
     * @param guard the single-resource guard
     * @return the aspect
     */
    @Bean
    @Conditional(MissingBeanCondition.class)
    public DataScopeCheckAspect dataScopeCheckAspect(DataScopeGuard guard) {
        return new DataScopeCheckAspect(guard);
    }
}
