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

import top.leafage.common.data.core.scope.enums.DataScopeType;

import java.util.Objects;
import java.util.Set;

/**
 * One data scope grant resolved for a resource and action.
 *
 * @param scope          the effective data scope
 * @param customSubjects owners configured for {@link DataScopeType#CUSTOM}
 * @author wq li
 */
public record DataScopeGrant(DataScopeType scope, Set<DataSubject> customSubjects) {

    /**
     * Creates an immutable grant.
     */
    public DataScopeGrant {
        Objects.requireNonNull(scope, "scope must not be null");
        customSubjects = customSubjects == null ? Set.of() : Set.copyOf(customSubjects);
    }

    /**
     * Creates a grant without configured owners.
     *
     * @param scope the effective data scope
     * @return a grant
     */
    public static DataScopeGrant of(DataScopeType scope) {
        return new DataScopeGrant(scope, Set.of());
    }
}
