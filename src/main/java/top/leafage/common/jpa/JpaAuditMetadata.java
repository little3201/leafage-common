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
package top.leafage.common.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * Jpa audit.
 *
 * @author wq li
 * @since 0.3.0
 */
@Embeddable
public class JpaAuditMetadata {

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 50)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private Instant createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", insertable = false, length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date", insertable = false)
    private Instant lastModifiedDate;


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

}

