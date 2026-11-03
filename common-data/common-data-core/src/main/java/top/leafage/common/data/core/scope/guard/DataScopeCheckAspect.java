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

package top.leafage.common.data.core.scope.guard;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import top.leafage.common.data.core.scope.annotation.DataScopeCheck;

import java.util.Objects;

/**
 * Enforces {@link DataScopeCheck} before the annotated method executes.
 *
 * @author wq li
 */
@Aspect
public class DataScopeCheckAspect {

    private final DataScopeGuard guard;

    /**
     * Creates the aspect.
     *
     * @param guard the single-resource guard
     */
    public DataScopeCheckAspect(DataScopeGuard guard) {
        this.guard = Objects.requireNonNull(guard, "guard must not be null");
    }

    /**
     * Checks access and then proceeds.
     *
     * @param joinPoint the join point
     * @param check     the annotation
     * @return the method result
     * @throws Throwable when the method throws or access is denied
     */
    @Around("@annotation(check)")
    public Object around(ProceedingJoinPoint joinPoint, DataScopeCheck check) throws Throwable {
        Object[] arguments = joinPoint.getArgs();
        int index = check.parameterIndex();
        if (index < 0 || index >= arguments.length) {
            throw new IllegalArgumentException("DataScopeCheck parameter index is out of bounds: "
                    + index);
        }
        String action = check.action().isBlank()
                ? ((MethodSignature) joinPoint.getSignature()).getMethod().getName()
                : check.action();
        guard.check(check.value(), action, arguments[index]);
        return joinPoint.proceed();
    }
}
