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
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;

/**
 * Creates {@link DataScopeJpaRepository} instances with injected support.
 *
 * @author wq li
 */
public class DataScopeJpaRepositoryFactory extends JpaRepositoryFactory {

    private final DataScopeJpaSupport support;

    /**
     * Creates the factory.
     *
     * @param entityManager the entity manager
     * @param support       data scope support
     */
    public DataScopeJpaRepositoryFactory(EntityManager entityManager, DataScopeJpaSupport support) {
        super(entityManager);
        this.support = support;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
                                                                    EntityManager entityManager) {
        JpaEntityInformation<?, ?> entityInformation = getEntityInformation(information.getDomainType());
        return new DataScopeJpaRepository(entityInformation, entityManager, support);
    }
}
