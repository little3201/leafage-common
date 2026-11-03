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

/**
 * The data scope of the currently executing operation.
 *
 * @param action   the operation, by default the service method name
 * @param resource an optional resource restriction; blank means every queried entity
 * @author wq li
 */
public record DataScopeContext(String action, String resource) {

    /**
     * Validates a context.
     */
    public DataScopeContext {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("action must not be blank");
        }
        if (resource == null) {
            resource = "";
        }
    }

    /**
     * Creates a context without a resource restriction.
     *
     * @param action the operation
     * @return the context
     */
    public static DataScopeContext of(String action) {
        return new DataScopeContext(action, "");
    }

    /**
     * Creates a context restricted to one resource.
     *
     * @param action   the operation
     * @param resource the resource code, or blank for every queried entity
     * @return the context
     */
    public static DataScopeContext of(String action, String resource) {
        return new DataScopeContext(action, resource);
    }
}
