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

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.ClassUtils;

/**
 * Registers a shared bean unless the application already declares one.
 *
 * <p>This keeps the core module free of Spring Boot while preserving the same
 * override behavior for adapters that import {@link DataScopeCoreConfiguration}.</p>
 *
 * @author wq li
 */
final class MissingBeanCondition implements Condition {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        if (!(metadata instanceof MethodMetadata methodMetadata)) {
            return true;
        }
        String typeName = methodMetadata.getReturnTypeName();
        try {
            Class<?> type = ClassUtils.forName(typeName, context.getClassLoader());
            if (context.getBeanFactory() == null) {
                return true;
            }
            return context.getBeanFactory().getBeanNamesForType(type, true, false).length == 0;
        } catch (ClassNotFoundException ex) {
            return true;
        }
    }
}
