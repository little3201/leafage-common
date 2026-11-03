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

import org.springframework.beans.factory.ObjectProvider;
import top.leafage.common.data.core.scope.DataScopeDecision;
import top.leafage.common.data.core.scope.DataScopeEngine;
import top.leafage.common.data.core.scope.DataSubject;

import java.util.Objects;
import java.util.Set;

/**
 * Enforces data scope for a single resource, typically before a write.
 *
 * <p>List queries are filtered by the persistence adapter; this guard covers
 * the case where a caller addresses one row directly.</p>
 *
 * @author wq li
 */
public final class DataScopeGuard {

    private final DataScopeEngine engine;
    private final ObjectProvider<DataScopeAccessChecker> accessChecker;

    /**
     * Creates the guard.
     *
     * @param engine        the decision engine
     * @param accessChecker the optional single-resource checker
     */
    public DataScopeGuard(DataScopeEngine engine,
                          ObjectProvider<DataScopeAccessChecker> accessChecker) {
        this.engine = Objects.requireNonNull(engine, "engine must not be null");
        this.accessChecker = Objects.requireNonNull(accessChecker, "accessChecker must not be null");
    }

    /**
     * Checks access to one resource row.
     *
     * @param resource   the resource code
     * @param action     the write operation
     * @param resourceId the resource primary key
     * @throws DataScopeDeniedException when access is denied
     */
    public void check(String resource, String action, Object resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId must not be null");
        }
        DataScopeDecision decision = engine.decide(resource, action);
        switch (decision) {
            case DataScopeDecision.Unrestricted unrestricted -> {
                // every row is visible
            }
            case DataScopeDecision.Denied deniedDecision ->
                    throw denied(resource, resourceId);
            case DataScopeDecision.Restricted restricted -> checkOwners(resource, resourceId, restricted);
        }
    }

    private void checkOwners(String resource, Object resourceId,
                             DataScopeDecision.Restricted restricted) {
        DataScopeAccessChecker checker = accessChecker.getIfAvailable();
        if (checker == null) {
            throw new DataScopeDeniedException(
                    "no DataScopeAccessChecker configured for resource: " + resource);
        }
        Set<DataSubject> owners = restricted.owners();
        if (!checker.isAccessible(resource, resourceId, owners)) {
            throw denied(resource, resourceId);
        }
    }

    private DataScopeDeniedException denied(String resource, Object resourceId) {
        return new DataScopeDeniedException(
                "access denied for resource: " + resource + " and id: " + resourceId);
    }
}
