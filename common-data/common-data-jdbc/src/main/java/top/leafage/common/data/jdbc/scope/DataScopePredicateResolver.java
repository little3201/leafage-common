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
import top.leafage.common.data.core.scope.DataSubject;

import java.util.Set;

/**
 * Builds the JDBC criteria for resolved owners.
 *
 * @author wq li
 */
public interface DataScopePredicateResolver {

    /**
     * Builds a visibility criterion.
     *
     * @param entityType the filtered entity type
     * @param resource   the resource code
     * @param owners     the owners allowed by the decision
     * @param <T>        the entity type
     * @return the criterion
     */
    <T> Criteria resolve(Class<T> entityType, String resource, Set<DataSubject> owners);

    /**
     * Builds a criterion that matches no row.
     *
     * @param entityType the filtered entity type
     * @param <T>        the entity type
     * @return the criterion
     */
    <T> Criteria deny(Class<T> entityType);
}
