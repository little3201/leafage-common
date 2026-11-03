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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository base class that applies data scope to every read and identifier
 * based operation.
 *
 * <p>Enable it with:</p>
 * <pre>
 * &#64;EnableJdbcRepositories(
 *         repositoryBaseClass = DataScopeJdbcRepository.class,
 *         repositoryFactoryBeanClass = DataScopeJdbcRepositoryFactoryBean.class)
 * </pre>
 *
 * <p>Derived query methods and {@code @Query} methods bypass this base class and
 * are therefore outside this interception; see the module documentation.</p>
 *
 * @param <T>  the entity type
 * @param <ID> the identifier type
 * @author wq li
 */
public class DataScopeJdbcRepository<T, ID>
        implements CrudRepository<T, ID>, PagingAndSortingRepository<T, ID> {

    private final JdbcAggregateOperations operations;
    private final RelationalPersistentEntity<T> entity;
    private final DataScopeJdbcSupport support;

    /**
     * Creates the repository.
     *
     * @param operations the JDBC aggregate operations
     * @param entity     entity metadata
     * @param converter  the JDBC converter, kept for signature parity
     * @param support    data scope support
     */
    public DataScopeJdbcRepository(JdbcAggregateOperations operations,
                                   RelationalPersistentEntity<T> entity,
                                   JdbcConverter converter,
                                   DataScopeJdbcSupport support) {
        this.operations = operations;
        this.entity = entity;
        this.support = support;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> S save(S entity) {
        return operations.save(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return operations.saveAll(entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> findById(ID id) {
        if (!support.active()) {
            return Optional.ofNullable(operations.findById(id, domainType()));
        }
        return operations.findOne(scoped(idCriteria(id)), domainType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(ID id) {
        if (!support.active()) {
            return operations.existsById(id, domainType());
        }
        return operations.exists(scoped(idCriteria(id)), domainType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll() {
        if (!support.active()) {
            return operations.findAll(domainType());
        }
        return operations.findAll(scoped(null), domainType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        if (!support.active()) {
            return operations.findAllById(ids, domainType());
        }
        List<ID> identifiers = new ArrayList<>();
        ids.forEach(identifiers::add);
        if (identifiers.isEmpty()) {
            return List.of();
        }
        return operations.findAll(scoped(Criteria.where(idProperty()).in(identifiers)),
                domainType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        if (!support.active()) {
            return operations.count(domainType());
        }
        return operations.count(scoped(null), domainType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(ID id) {
        if (!support.active()) {
            operations.deleteById(id, domainType());
            return;
        }
        findById(id).ifPresent(operations::delete);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(T entity) {
        operations.delete(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        if (!support.active()) {
            operations.deleteAllById(ids, domainType());
            return;
        }
        List<ID> identifiers = new ArrayList<>();
        ids.forEach(identifiers::add);
        findAllById(identifiers).forEach(operations::delete);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        operations.deleteAll(entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        if (!support.active()) {
            operations.deleteAll(domainType());
            return;
        }
        operations.deleteAll(findAll());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll(Sort sort) {
        Assert.notNull(sort, "Sort must not be null");
        if (!support.active()) {
            return operations.findAll(domainType(), sort);
        }
        return operations.findAll(scoped(null).sort(sort), domainType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> findAll(Pageable pageable) {
        Assert.notNull(pageable, "Pageable must not be null");
        Query query = scoped(null).with(pageable);
        List<T> content = operations.findAll(query, domainType());
        return PageableExecutionUtils.getPage(content, pageable,
                () -> count());
    }

    private Query scoped(Criteria criteria) {
        Criteria combined = support.combine(criteria, domainType());
        return Query.query(combined == null ? CriteriaDefinition.empty() : combined);
    }

    private Criteria idCriteria(Object id) {
        return Criteria.where(idProperty()).is(id);
    }

    private Class<T> domainType() {
        return entity.getType();
    }

    private String idProperty() {
        RelationalPersistentProperty id = entity.getRequiredIdProperty();
        if (id.isEmbedded()) {
            throw new UnsupportedOperationException(
                    "Data scope does not support composite identifiers: "
                            + domainType().getName());
        }
        return id.getName();
    }
}
