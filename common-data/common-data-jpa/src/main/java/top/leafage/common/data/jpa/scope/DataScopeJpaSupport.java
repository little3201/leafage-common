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

package top.leafage.common.data.jpa.scope;

import org.springframework.data.jpa.domain.Specification;
import top.leafage.common.data.core.scope.DataScopeDecision;
import top.leafage.common.data.core.scope.DataScopeEngine;
import top.leafage.common.data.core.scope.context.DataScopeContextHolder;

import java.util.Objects;

/**
 * Combines business specifications with the current data scope decision.
 *
 * @author wq li
 */
public final class DataScopeJpaSupport {

    private final DataScopeEngine engine;
    private final DataScopePredicateResolver predicateResolver;
    private final ResourceResolver resourceResolver;

    /**
     * Creates the support component.
     *
     * @param engine            the decision engine
     * @param predicateResolver builds the visibility predicate
     * @param resourceResolver  maps entities to resource codes
     */
    public DataScopeJpaSupport(DataScopeEngine engine,
                               DataScopePredicateResolver predicateResolver,
                               ResourceResolver resourceResolver) {
        this.engine = Objects.requireNonNull(engine, "engine must not be null");
        this.predicateResolver = Objects.requireNonNull(predicateResolver,
                "predicateResolver must not be null");
        this.resourceResolver = Objects.requireNonNull(resourceResolver,
                "resourceResolver must not be null");
    }

    private static <T> Specification<T> denied() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
    }

    private static <T> Specification<T> and(Specification<T> left, Specification<T> right) {
        return left == null ? right : left.and(right);
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
     * Adds the data scope predicate to a business specification.
     *
     * @param specification the business specification, may be {@code null}
     * @param domainClass   the filtered entity type
     * @param <T>           the entity type
     * @return the combined specification
     */
    public <T> Specification<T> combine(Specification<T> specification, Class<T> domainClass) {
        if (!active()) {
            return specification;
        }
        String resource = resourceResolver.resolve(domainClass);
        String scopedResource = DataScopeContextHolder.currentResource();
        if (scopedResource != null && !scopedResource.equals(resource)) {
            return specification;
        }
        String action = DataScopeContextHolder.currentAction();
        if (action == null || action.isBlank()) {
            return specification;
        }
        DataScopeDecision decision = engine.decide(resource, action);
        return switch (decision) {
            case DataScopeDecision.Unrestricted unrestricted -> specification;
            case DataScopeDecision.Denied denied -> and(specification, denied());
            case DataScopeDecision.Restricted restricted -> and(specification,
                    predicateResolver.resolve(domainClass, resource, restricted.owners()));
        };
    }
}
