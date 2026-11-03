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

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.leafage.common.data.core.scope.DataScopeEngine;
import top.leafage.common.data.core.scope.DataScopePolicyProvider;
import top.leafage.common.data.core.scope.DataSubjectResolver;
import top.leafage.common.data.core.scope.guard.DataScopeGuard;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class DataScopeCoreConfigurationTest {

    @Test
    void keepsAnApplicationProvidedEngine() {
        DataScopePolicyProvider provider = (resource, action) -> List.of();
        DataSubjectResolver resolver = scope -> Set.of();
        DataScopeEngine custom = new DataScopeEngine(provider, resolver);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(DataScopePolicyProvider.class, () -> provider);
            context.registerBean(DataSubjectResolver.class, () -> resolver);
            context.registerBean("customEngine", DataScopeEngine.class, () -> custom);
            context.register(DataScopeCoreConfiguration.class);
            context.refresh();

            assertSame(custom, context.getBean(DataScopeEngine.class));
            assertEquals(1, context.getBeansOfType(DataScopeEngine.class).size());
            assertNotNull(context.getBean(DataScopeGuard.class));
        }
    }
}
