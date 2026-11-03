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

package top.leafage.common.data.core.scope;

import java.util.List;

/**
 * Loads the effective data scope grants for one resource and action.
 *
 * <p>The implementation is application specific: it knows how policies are
 * stored and how the current caller is resolved. The default JPA adapter does
 * not require any particular entity or table.</p>
 *
 * @author wq li
 */
public interface DataScopePolicyProvider {

    /**
     * Resolves grants for the current caller.
     *
     * @param resource the resource code, for example a table name
     * @param action   the current operation, by default the service method name
     * @return all grants; an empty list denies access when filtering is active
     */
    List<DataScopeGrant> grantsFor(String resource, String action);
}
