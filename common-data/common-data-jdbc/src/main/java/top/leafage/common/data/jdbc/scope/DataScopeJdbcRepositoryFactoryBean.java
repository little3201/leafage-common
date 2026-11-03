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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;

/**
 * Repository factory bean that injects {@link DataScopeJdbcSupport} into
 * the base repository.
 *
 * @param <T>  the repository type
 * @param <S>  the entity type
 * @param <ID> the identifier type
 * @author wq li
 */
public class DataScopeJdbcRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends JdbcRepositoryFactoryBean<T, S, ID> {

    private BeanFactory beanFactory;
    private JdbcAggregateOperations aggregateOperations;
    private ApplicationEventPublisher publisher;
    private QueryMappingConfiguration queryMappingConfiguration;

    /**
     * Creates the factory bean.
     *
     * @param repositoryInterface the repository interface
     */
    public DataScopeJdbcRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);
        this.beanFactory = beanFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        super.setApplicationEventPublisher(publisher);
        this.publisher = publisher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJdbcAggregateOperations(JdbcAggregateOperations operations) {
        super.setJdbcAggregateOperations(operations);
        this.aggregateOperations = operations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQueryMappingConfiguration(QueryMappingConfiguration configuration) {
        super.setQueryMappingConfiguration(configuration);
        this.queryMappingConfiguration = configuration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        if (beanFactory == null) {
            throw new IllegalStateException("BeanFactory is required for data scope repositories");
        }
        if (aggregateOperations == null) {
            throw new IllegalStateException(
                    "JdbcAggregateOperations is required for data scope repositories");
        }
        DataScopeJdbcSupport support = beanFactory.getBean(DataScopeJdbcSupport.class);
        DataScopeJdbcRepositoryFactory factory =
                new DataScopeJdbcRepositoryFactory(aggregateOperations, support);
        factory.setBeanFactory(beanFactory);
        if (queryMappingConfiguration != null) {
            factory.setQueryMappingConfiguration(queryMappingConfiguration);
        }
        factory.setApplicationEventPublisher(publisher);
        return factory;
    }
}
