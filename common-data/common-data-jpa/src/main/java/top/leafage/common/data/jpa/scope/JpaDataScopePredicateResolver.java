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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import top.leafage.common.data.core.scope.DataSubject;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Builds an {@code exists} predicate against an application-owned mapping entity.
 *
 * <p>The generated condition is equivalent to:</p>
 * <pre>
 * exists (select 1 from mapping m
 *         where m.resource = :resource
 *           and m.resourceId = root.id
 *           and (m.subjectType, m.subjectId) in (...))
 * </pre>
 *
 * @author wq li
 */
public class JpaDataScopePredicateResolver implements DataScopePredicateResolver {

    private final ResourceSubjectMapping mapping;

    /**
     * Creates the resolver.
     *
     * @param mapping the application mapping configuration
     */
    public JpaDataScopePredicateResolver(ResourceSubjectMapping mapping) {
        this.mapping = Objects.requireNonNull(mapping, "mapping must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Specification<T> resolve(Class<T> entityType, String resource, Set<DataSubject> owners) {
        if (owners == null || owners.isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
        }
        return (root, query, criteriaBuilder) -> {
            Objects.requireNonNull(query, "criteria query must not be null");
            Subquery<Integer> ownership = query.subquery(Integer.class);
            Root<?> mappingRoot = ownership.from(mapping.entityType());
            ownership.select(criteriaBuilder.literal(1))
                    .where(criteriaBuilder.equal(mappingRoot.get(mapping.resourceProperty()), resource),
                            criteriaBuilder.equal(mappingRoot.get(mapping.resourceIdProperty()),
                                    root.get(mapping.rootIdProperty())),
                            criteriaBuilder.or(ownerPredicates(mappingRoot, owners, criteriaBuilder)));
            return criteriaBuilder.exists(ownership);
        };
    }

    private Predicate[] ownerPredicates(Root<?> mappingRoot, Set<DataSubject> owners,
                                        CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = owners.stream()
                .map(owner -> criteriaBuilder.and(
                        criteriaBuilder.equal(mappingRoot.get(mapping.subjectTypeProperty()), owner.type()),
                        criteriaBuilder.equal(mappingRoot.get(mapping.subjectIdProperty()), owner.id())))
                .toList();
        return predicates.toArray(Predicate[]::new);
    }
}
