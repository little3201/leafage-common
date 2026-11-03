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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * Repository factory bean that injects {@link DataScopeJpaSupport} into
 * the base repository.
 *
 * @param <T>  the repository type
 * @param <S>  the entity type
 * @param <ID> the identifier type
 * @author wq li
 */
public class DataScopeJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
        extends JpaRepositoryFactoryBean<T, S, ID> {

    private BeanFactory beanFactory;

    /**
     * Creates the factory bean.
     *
     * @param repositoryInterface the repository interface
     */
    public DataScopeJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
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
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        if (beanFactory == null) {
            throw new IllegalStateException("BeanFactory is required for data scope repositories");
        }
        DataScopeJpaSupport support = beanFactory.getBean(DataScopeJpaSupport.class);
        return new DataScopeJpaRepositoryFactory(entityManager, support);
    }
}
