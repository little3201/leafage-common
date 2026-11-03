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

package top.leafage.common.data.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Application-owned mapping entity used by the integration test.
 *
 * @author wq li
 */
@Entity
@Table(name = "resource_subjects")
public class ResourceSubjectRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resource;

    private Long resourceId;

    private String subjectType;

    private String subjectId;

    /**
     * Creates an empty entity for JPA.
     */
    protected ResourceSubjectRow() {
    }

    /**
     * Creates a mapping row.
     *
     * @param resource    the resource code
     * @param resourceId  the resource identifier
     * @param subjectType the subject type
     * @param subjectId   the subject identifier
     */
    public ResourceSubjectRow(String resource, Long resourceId, String subjectType, String subjectId) {
        this.resource = resource;
        this.resourceId = resourceId;
        this.subjectType = subjectType;
        this.subjectId = subjectId;
    }

    /**
     * Returns the identifier.
     *
     * @return the identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the resource code.
     *
     * @return the resource code
     */
    public String getResource() {
        return resource;
    }

    /**
     * Returns the resource identifier.
     *
     * @return the resource identifier
     */
    public Long getResourceId() {
        return resourceId;
    }

    /**
     * Returns the subject type.
     *
     * @return the subject type
     */
    public String getSubjectType() {
        return subjectType;
    }

    /**
     * Returns the subject identifier.
     *
     * @return the subject identifier
     */
    public String getSubjectId() {
        return subjectId;
    }
}
