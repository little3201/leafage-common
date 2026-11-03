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

package top.leafage.common.data.core.scope;

/**
 * A subject that owns data, for example a user or a group.
 *
 * <p>This type is deliberately storage neutral: the JPA, JDBC and R2DBC adapters
 * only rely on the string codes and never on an entity class.</p>
 *
 * @param type the application-defined subject type code
 * @param id   the application-defined subject identifier
 * @author wq li
 */
public record DataSubject(String type, String id) {

    /**
     * Validates a subject.
     */
    public DataSubject {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
    }
}
