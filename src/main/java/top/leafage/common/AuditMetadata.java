/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package top.leafage.common;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;

import java.time.Instant;

/**
 * audit.
 *
 * @author wq li
 * @since 0.3.0
 */
public class AuditMetadata {

    @InsertOnlyProperty
    @CreatedBy
    @Column(value = "created_by")
    private String createdBy;

    @InsertOnlyProperty
    @CreatedDate
    @Column(value = "created_date")
    private Instant createdDate;

    @LastModifiedBy
    @Column(value = "last_modified_by")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(value = "last_modified_date")
    private Instant lastModifiedDate;


    /**
     * <p>Getter for the field <code>createdBy</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCreatedBy() {
        return this.createdBy;
    }

    /**
     * <p>Setter for the field <code>createdBy</code>.</p>
     *
     * @param createdBy a {@link java.lang.String} object
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * <p>Getter for the field <code>createdDate</code>.</p>
     *
     * @return a {@link java.time.Instant} object
     */
    public Instant getCreatedDate() {
        return this.createdDate;
    }

    /**
     * <p>Setter for the field <code>createdDate</code>.</p>
     *
     * @param creationDate a {@link java.time.Instant} object
     */
    public void setCreatedDate(Instant creationDate) {
        this.createdDate = creationDate;
    }

    /**
     * <p>Getter for the field <code>lastModifiedBy</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    /**
     * <p>Setter for the field <code>lastModifiedBy</code>.</p>
     *
     * @param lastModifiedBy a {@link java.lang.String} object
     */
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /**
     * <p>Getter for the field <code>lastModifiedDate</code>.</p>
     *
     * @return a {@link java.time.Instant} object
     */
    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    /**
     * <p>Setter for the field <code>lastModifiedDate</code>.</p>
     *
     * @param lastModifiedDate a {@link java.time.Instant} object
     */
    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}

