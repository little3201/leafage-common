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

import java.util.Objects;

/**
 * Performs trusted single-resource data-permission checks.
 *
 * @author wq li
 */
public class DataPermissionGuard {

    private final DataPermissionContextProvider contextProvider;
    private final DataPermissionEvaluator evaluator;
    private final DataResourceSubjectStore subjectStore;

    public DataPermissionGuard(DataPermissionContextProvider contextProvider,
                               DataPermissionEvaluator evaluator,
                               DataResourceSubjectStore subjectStore) {
        this.contextProvider = Objects.requireNonNull(contextProvider, "contextProvider must not be null");
        this.evaluator = Objects.requireNonNull(evaluator, "evaluator must not be null");
        this.subjectStore = Objects.requireNonNull(subjectStore, "subjectStore must not be null");
    }

    public DataAccessScope resolve(DataPermissionRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        DataPermissionContext context = Objects.requireNonNull(
                        contextProvider.currentContext(), "currentContext must not return null")
                .orElseThrow(() -> new DataPermissionDeniedException("an authenticated principal is required"));
        return Objects.requireNonNull(
                evaluator.evaluate(context, request), "evaluator must not return null");
    }

    public void check(DataPermissionRequest request, Long resourceId) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(resourceId, "resourceId must not be null");
        DataAccessScope scope = resolve(request);
        if (!scope.all() && (scope.isEmpty() || !subjectStore.isAccessible(request.resource(), resourceId, scope))) {
            throw new DataPermissionDeniedException("access denied for resource: " + request.resource());
        }
    }
}
