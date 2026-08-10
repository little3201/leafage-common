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

package top.leafage.common.data.permission;

import java.util.Set;

/**
 * The resolved data access scope for one resource operation.
 *
 * @param scopeTypes the policies that contributed to this result
 * @param subjects the resource subjects visible to the caller
 * @param all whether every resource is visible
 * @author wq li
 */
public record DataAccessScope(Set<DataScopeType> scopeTypes, Set<DataSubject> subjects, boolean all) {

    public DataAccessScope {
        scopeTypes = Set.copyOf(scopeTypes);
        subjects = Set.copyOf(subjects);
        if (all != scopeTypes.contains(DataScopeType.ALL)) {
            throw new IllegalArgumentException("all must match the presence of ALL scope type");
        }
        if (scopeTypes.contains(DataScopeType.NONE) && (!subjects.isEmpty() || scopeTypes.size() > 1)) {
            throw new IllegalArgumentException("NONE scope cannot be combined with subjects or other scope types");
        }
    }

    public static DataAccessScope none() {
        return new DataAccessScope(Set.of(DataScopeType.NONE), Set.of(), false);
    }

    public static DataAccessScope allAccess() {
        return new DataAccessScope(Set.of(DataScopeType.ALL), Set.of(), true);
    }

    public boolean isEmpty() {
        return !all && subjects.isEmpty();
    }
}
