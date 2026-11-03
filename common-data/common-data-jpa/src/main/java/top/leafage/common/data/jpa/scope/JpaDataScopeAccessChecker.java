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

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import top.leafage.common.data.core.scope.DataSubject;
import top.leafage.common.data.core.scope.guard.DataScopeAccessChecker;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * JPA implementation of single-resource access checks.
 *
 * @author wq li
 */
public final class JpaDataScopeAccessChecker implements DataScopeAccessChecker {

    private final EntityManager entityManager;
    private final ResourceSubjectMapping mapping;

    /**
     * Creates the checker.
     *
     * @param entityManager the entity manager
     * @param mapping       the application mapping configuration
     */
    public JpaDataScopeAccessChecker(EntityManager entityManager, ResourceSubjectMapping mapping) {
        this.entityManager = Objects.requireNonNull(entityManager, "entityManager must not be null");
        this.mapping = Objects.requireNonNull(mapping, "mapping must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccessible(String resource, Object resourceId, Set<DataSubject> owners) {
        if (owners == null || owners.isEmpty()) {
            return false;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<?> root = query.from(mapping.entityType());
        query.select(criteriaBuilder.count(root))
                .where(criteriaBuilder.equal(root.get(mapping.resourceProperty()), resource),
                        criteriaBuilder.equal(root.get(mapping.resourceIdProperty()), resourceId),
                        criteriaBuilder.or(ownerPredicates(root, owners, criteriaBuilder)));
        Long count = entityManager.createQuery(query).getSingleResult();
        return count != null && count > 0;
    }

    private Predicate[] ownerPredicates(Root<?> root, Set<DataSubject> owners,
                                        CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = owners.stream()
                .map(owner -> criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(mapping.subjectTypeProperty()), owner.type()),
                        criteriaBuilder.equal(root.get(mapping.subjectIdProperty()), owner.id())))
                .toList();
        return predicates.toArray(Predicate[]::new);
    }
}
