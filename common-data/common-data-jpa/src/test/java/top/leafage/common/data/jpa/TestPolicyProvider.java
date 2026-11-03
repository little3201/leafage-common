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

package top.leafage.common.data.jpa;

import top.leafage.common.data.core.scope.DataScopeGrant;
import top.leafage.common.data.core.scope.DataScopePolicyProvider;
import top.leafage.common.data.core.scope.DataSubject;
import top.leafage.common.data.core.scope.enums.DataScopeType;

import java.util.List;
import java.util.Set;

/**
 * Mutable provider used by the integration test.
 *
 * @author wq li
 */
public class TestPolicyProvider implements DataScopePolicyProvider {

    private volatile DataScopeType scope = DataScopeType.CUSTOM;

    private volatile Set<DataSubject> customSubjects = Set.of();

    /**
     * Changes the returned policy.
     *
     * @param scope          the scope, or {@code null} for no grant
     * @param customSubjects owners for CUSTOM
     */
    public void reset(DataScopeType scope, Set<DataSubject> customSubjects) {
        this.scope = scope;
        this.customSubjects = customSubjects == null ? Set.of() : Set.copyOf(customSubjects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataScopeGrant> grantsFor(String resource, String action) {
        DataScopeType current = scope;
        if (current == null) {
            return List.of();
        }
        return List.of(new DataScopeGrant(current, customSubjects));
    }
}
