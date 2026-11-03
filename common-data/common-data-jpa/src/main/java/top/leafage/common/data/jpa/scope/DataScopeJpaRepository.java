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
import jakarta.persistence.TypedQuery;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository base class that applies data scope to every specification query.
 *
 * <p>Enable it with:</p>
 * <pre>
 * &#64;EnableJpaRepositories(
 *         repositoryBaseClass = DataScopeJpaRepository.class,
 *         repositoryFactoryBeanClass = DataScopeJpaRepositoryFactoryBean.class)
 * </pre>
 *
 * <p>Derived query methods and {@code @Query} methods are not routed through
 * {@code getQuery} and are therefore outside this interception; see the module
 * documentation.</p>
 *
 * @param <T>  the entity type
 * @param <ID> the identifier type
 * @author wq li
 */
public class DataScopeJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final DataScopeJpaSupport support;

    /**
     * Creates the repository.
     *
     * @param entityInformation entity metadata
     * @param entityManager     the entity manager
     * @param support           data scope support
     */
    public DataScopeJpaRepository(JpaEntityInformation<T, ?> entityInformation,
                                  EntityManager entityManager,
                                  DataScopeJpaSupport support) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.support = support;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <S extends T> TypedQuery<S> getQuery(Specification<S> spec, Class<S> domainClass, Sort sort) {
        return super.getQuery(support.combine(spec, domainClass), domainClass, sort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <S extends T> TypedQuery<S> getQuery(Specification<S> spec, Class<S> domainClass,
                                                   Pageable pageable) {
        return super.getQuery(support.combine(spec, domainClass), domainClass, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <S extends T> TypedQuery<Long> getCountQuery(Specification<S> spec, Class<S> domainClass) {
        return super.getCountQuery(support.combine(spec, domainClass), domainClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> findById(ID id) {
        if (!support.active()) {
            return super.findById(id);
        }
        requireSimpleId();
        Specification<T> idSpecification = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(idAttributeName()), id);
        return findOne(idSpecification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        if (!support.active()) {
            return super.findAllById(ids);
        }
        requireSimpleId();
        List<ID> identifiers = new ArrayList<>();
        ids.forEach(identifiers::add);
        if (identifiers.isEmpty()) {
            return List.of();
        }
        Specification<T> idSpecification = (root, query, criteriaBuilder) ->
                root.get(idAttributeName()).in(identifiers);
        return findAll(idSpecification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(Specification<T> spec) {
        return super.exists(support.combine(spec, getDomainClass()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return count((Specification<T>) null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllInBatch() {
        deleteAllInBatch(findAll());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllByIdInBatch(Iterable<ID> ids) {
        deleteAllInBatch(findAllById(ids));
    }

    @SuppressWarnings("unchecked")
    private String idAttributeName() {
        SingularAttribute<? super T, ?> attribute = entityInformation.getIdAttribute();
        if (attribute == null) {
            throw new IllegalStateException("Data scope requires an id attribute on "
                    + getDomainClass().getName());
        }
        return attribute.getName();
    }

    private void requireSimpleId() {
        if (entityInformation.hasCompositeId()) {
            throw new UnsupportedOperationException(
                    "Data scope does not support composite identifiers: "
                            + getDomainClass().getName());
        }
    }
}
