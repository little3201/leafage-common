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

import top.leafage.common.data.core.scope.context.DataScopeContextHolder;
import top.leafage.common.data.core.scope.enums.DataScopeType;

import java.util.*;

/**
 * Evaluates grants into a storage-neutral decision.
 *
 * <p>Decisions are cached per thread and per {@link DataScopeContextHolder}
 * scope, so one query never loads the same policy twice.</p>
 *
 * @author wq li
 */
public final class DataScopeEngine {

    private final DataScopePolicyProvider policyProvider;
    private final DataSubjectResolver subjectResolver;
    private final ThreadLocal<Cache> cache = ThreadLocal.withInitial(Cache::new);

    /**
     * Creates the engine.
     *
     * @param policyProvider  loads grants for the current caller
     * @param subjectResolver resolves owners for limited scopes
     */
    public DataScopeEngine(DataScopePolicyProvider policyProvider,
                           DataSubjectResolver subjectResolver) {
        this.policyProvider = Objects.requireNonNull(policyProvider, "policyProvider must not be null");
        this.subjectResolver = Objects.requireNonNull(subjectResolver, "subjectResolver must not be null");
    }

    /**
     * Decides visibility for a resource and action.
     *
     * <p>No grant means denied: filtering is only activated explicitly (see
     * {@code @DataScope}), so an activated resource without a policy is a
     * configuration error and must not leak data.</p>
     *
     * @param resource the resource code
     * @param action   the current operation
     * @return the decision
     */
    public DataScopeDecision decide(String resource, String action) {
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("resource must not be blank");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("action must not be blank");
        }
        Cache current = currentCache();
        return current.decisions.computeIfAbsent(new DecisionKey(resource, action),
                key -> evaluate(key.resource(), key.action()));
    }

    /**
     * Clears decisions cached for the current thread.
     */
    public void clearCache() {
        cache.remove();
    }

    private DataScopeDecision evaluate(String resource, String action) {
        List<DataScopeGrant> grants = policyProvider.grantsFor(resource, action);
        if (grants == null || grants.isEmpty()) {
            return new DataScopeDecision.Denied();
        }
        if (grants.stream().anyMatch(grant -> grant.scope() == DataScopeType.ALL)) {
            return new DataScopeDecision.Unrestricted();
        }

        Set<DataSubject> owners = new LinkedHashSet<>();
        for (DataScopeGrant grant : grants) {
            if (grant.scope() == DataScopeType.NONE) {
                continue;
            }
            if (grant.scope() == DataScopeType.CUSTOM) {
                owners.addAll(grant.customSubjects());
                continue;
            }
            Set<DataSubject> resolved = subjectResolver.resolve(grant.scope());
            if (resolved != null) {
                owners.addAll(resolved);
            }
        }
        return owners.isEmpty() ? new DataScopeDecision.Denied()
                : new DataScopeDecision.Restricted(owners);
    }

    private Cache currentCache() {
        Cache current = cache.get();
        long generation = DataScopeContextHolder.generation();
        if (current.generation != generation) {
            current.generation = generation;
            current.decisions.clear();
        }
        return current;
    }

    private record DecisionKey(String resource, String action) {
    }

    private static final class Cache {
        private final Map<DecisionKey, DataScopeDecision> decisions = new HashMap<>();
        private long generation = -1;
    }
}
