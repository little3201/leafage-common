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

package top.leafage.common.data.core.scope.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Checks access to a single resource before a write method runs.
 *
 * <p>The interceptor reads the resource identifier from the configured method
 * parameter (the first one by default) and throws
 * {@code DataScopeDeniedException} when access is denied.</p>
 *
 * @author wq li
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScopeCheck {

    /**
     * The resource code, for example a table name.
     *
     * @return the resource code
     */
    String value();

    /**
     * The method parameter holding the resource identifier.
     *
     * @return the zero-based parameter index
     */
    int parameterIndex() default 0;

    /**
     * Overrides the action recognized by the policy provider.
     *
     * @return the action, or an empty string to use the method name
     */
    String action() default "";
}
