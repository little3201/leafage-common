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

package top.leafage.common.data.jdbc.scope;

import org.springframework.data.relational.core.query.Criteria;
import top.leafage.common.data.core.scope.DataScopeDecision;
import top.leafage.common.data.core.scope.DataScopeEngine;
import top.leafage.common.data.core.scope.context.DataScopeContextHolder;

import java.util.Objects;

/**
 * Combines business criteria with the current data scope decision.
 *
 * @author wq li
 */
public final class DataScopeJdbcSupport {

    private final DataScopeEngine engine;
    private final DataScopePredicateResolver predicateResolver;
    private final ResourceResolver resourceResolver;

    /**
     * Creates the support component.
     *
     * @param engine            the decision engine
     * @param predicateResolver builds the visibility criteria
     * @param resourceResolver  maps entities to resource codes
     */
    public DataScopeJdbcSupport(DataScopeEngine engine,
                                DataScopePredicateResolver predicateResolver,
                                ResourceResolver resourceResolver) {
        this.engine = Objects.requireNonNull(engine, "engine must not be null");
        this.predicateResolver = Objects.requireNonNull(predicateResolver,
                "predicateResolver must not be null");
        this.resourceResolver = Objects.requireNonNull(resourceResolver,
                "resourceResolver must not be null");
    }

    /**
     * Checks whether filtering is currently active.
     *
     * @return {@code true} inside a {@code @DataScope} scope
     */
    public boolean active() {
        return DataScopeContextHolder.isActive();
    }

    /**
     * Adds the data scope criterion to a business criterion.
     *
     * @param criteria   the business criteria, may be {@code null}
     * @param entityType the filtered entity type
     * @return the combined criteria, or {@code null} when neither side applies
     */
    public Criteria combine(Criteria criteria, Class<?> entityType) {
        if (!active()) {
            return criteria;
        }
        String resource = resourceResolver.resolve(entityType);
        String scopedResource = DataScopeContextHolder.currentResource();
        if (scopedResource != null && !scopedResource.equals(resource)) {
            return criteria;
        }
        String action = DataScopeContextHolder.currentAction();
        if (action == null || action.isBlank()) {
            return criteria;
        }
        DataScopeDecision decision = engine.decide(resource, action);
        return switch (decision) {
            case DataScopeDecision.Unrestricted unrestricted -> criteria;
            case DataScopeDecision.Denied denied ->
                    and(criteria, predicateResolver.deny(entityType));
            case DataScopeDecision.Restricted restricted -> and(criteria,
                    predicateResolver.resolve(entityType, resource, restricted.owners()));
        };
    }

    private static Criteria and(Criteria left, Criteria right) {
        if (left == null || left.isEmpty()) {
            return right;
        }
        if (right == null || right.isEmpty()) {
            return left;
        }
        return left.and(right);
    }
}
