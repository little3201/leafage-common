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

package top.leafage.common.data.core.scope.context;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.leafage.common.data.core.scope.annotation.DataScope;
import top.leafage.common.data.core.scope.annotation.DataScopeIgnore;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Publishes the current action around {@link DataScope} beans.
 *
 * <p>The action is the service method name unless the annotation overrides it.
 * The annotation value optionally restricts filtering to one resource; when it
 * is blank the persistence adapter derives a resource from each queried entity.</p>
 *
 * @author wq li
 */
@Aspect
public class DataScopeContextAspect {

    /**
     * Wraps annotated types and methods.
     *
     * @param joinPoint the join point
     * @return the method result
     * @throws Throwable when the method throws
     */
    @Around("@within(top.leafage.common.data.core.scope.annotation.DataScope)"
            + " || @annotation(top.leafage.common.data.core.scope.annotation.DataScope)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = AopUtils.getTargetClass(joinPoint.getTarget());

        DataScope methodAnnotation =
                AnnotatedElementUtils.findMergedAnnotation(method, DataScope.class);
        DataScope classAnnotation =
                AnnotatedElementUtils.findMergedAnnotation(targetClass, DataScope.class);

        DataScopeContext context = ignored(method, targetClass)
                ? DataScopeContext.ignoredContext()
                : DataScopeContext.of(action(method, methodAnnotation, classAnnotation),
                        resource(methodAnnotation, classAnnotation));
        DataScopeContextHolder.push(Objects.requireNonNull(context));
        try {
            return joinPoint.proceed();
        } finally {
            DataScopeContextHolder.pop();
        }
    }

    private boolean ignored(Method method, Class<?> targetClass) {
        return AnnotatedElementUtils.hasAnnotation(method, DataScopeIgnore.class)
                || AnnotatedElementUtils.hasAnnotation(targetClass, DataScopeIgnore.class);
    }

    private String action(Method method, DataScope methodAnnotation,
                          DataScope classAnnotation) {
        if (methodAnnotation != null && !methodAnnotation.action().isBlank()) {
            return methodAnnotation.action();
        }
        if (classAnnotation != null && !classAnnotation.action().isBlank()) {
            return classAnnotation.action();
        }
        return method.getName();
    }

    private String resource(DataScope methodAnnotation, DataScope classAnnotation) {
        if (methodAnnotation != null && !methodAnnotation.value().isBlank()) {
            return methodAnnotation.value();
        }
        return classAnnotation == null ? "" : classAnnotation.value();
    }
}
