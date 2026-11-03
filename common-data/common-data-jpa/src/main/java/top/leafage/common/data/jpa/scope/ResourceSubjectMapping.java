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

import java.util.Objects;

/**
 * Describes an application-owned entity that maps resources to subjects.
 *
 * <p>The library deliberately ships no entity: the table structure and the
 * entity name stay under the application's control. Only the entity class and
 * property names are configured.</p>
 *
 * @param entityType          the application mapping entity
 * @param resourceProperty    property holding the resource code
 * @param resourceIdProperty  property holding the resource primary key
 * @param subjectTypeProperty property holding the subject type
 * @param subjectIdProperty   property holding the subject identifier
 * @param rootIdProperty      primary key property of the filtered entity
 * @author wq li
 */
public record ResourceSubjectMapping(Class<?> entityType,
                                     String resourceProperty,
                                     String resourceIdProperty,
                                     String subjectTypeProperty,
                                     String subjectIdProperty,
                                     String rootIdProperty) {

    /**
     * Creates a mapping with default property names.
     */
    public ResourceSubjectMapping {
        Objects.requireNonNull(entityType, "entityType must not be null");
        resourceProperty = defaultName(resourceProperty, "resource");
        resourceIdProperty = defaultName(resourceIdProperty, "resourceId");
        subjectTypeProperty = defaultName(subjectTypeProperty, "subjectType");
        subjectIdProperty = defaultName(subjectIdProperty, "subjectId");
        rootIdProperty = defaultName(rootIdProperty, "id");
    }

    /**
     * Creates a mapping using the conventional property names.
     *
     * @param entityType the application mapping entity
     * @return the mapping
     */
    public static ResourceSubjectMapping of(Class<?> entityType) {
        return new ResourceSubjectMapping(entityType, "resource", "resourceId",
                "subjectType", "subjectId", "id");
    }

    private static String defaultName(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
