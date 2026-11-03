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
 * Activates data scope for a service type or method.
 *
 * <p>The action defaults to the intercepted method name, for example
 * {@code retrieve}, {@code fetch}, {@code create}, {@code modify} or
 * {@code remove}. The resource is resolved from the queried entity, so a
 * service touching several entities filters each of them independently.</p>
 *
 * @author wq li
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * Restricts filtering to one resource, for example a table code.
     *
     * <p>When set, queries against other entities in the same method are left
     * untouched. This is useful for a service that reads a primary entity and
     * its child entities: only the primary resource is filtered.</p>
     *
     * @return the resource code, or an empty string to filter every queried entity
     */
    String value() default "";

    /**
     * Overrides the action recognized by the policy provider.
     *
     * @return the action, or an empty string to use the method name
     */
    String action() default "";
}
