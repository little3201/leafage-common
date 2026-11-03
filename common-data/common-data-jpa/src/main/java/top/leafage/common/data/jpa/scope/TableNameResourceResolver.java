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

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Resolves the resource from {@link Table#name()}, then {@link Entity#name()}.
 *
 * <p>Applications with a different convention can replace this bean.</p>
 *
 * @author wq li
 */
public class TableNameResourceResolver implements ResourceResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolve(Class<?> entityType) {
        Table table = entityType.getAnnotation(Table.class);
        if (table != null && !table.name().isBlank()) {
            return table.name();
        }
        Entity entity = entityType.getAnnotation(Entity.class);
        if (entity != null && !entity.name().isBlank()) {
            return entity.name();
        }
        return entityType.getSimpleName();
    }
}
