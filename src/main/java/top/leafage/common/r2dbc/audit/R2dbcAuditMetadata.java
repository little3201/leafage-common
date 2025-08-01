/*
 * Copyright (c) 2025.  little3201.
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
package top.leafage.common.r2dbc.audit;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;

import java.time.Instant;

/**
 * Abstract class representing audit metadata for r2dbc entities.
 * Provides fields and methods for managing audit information such as entity creation,
 * modification details, and whether the entity is enabled. Intended to be extended
 * by entity classes that require audit tracking in a r2dbc context.
 * <p>
 * Annotations like {@code @InsertOnlyProperty} and {@code @CreatedDate} handle
 * database insertion and automatic timestamping for creation events.
 * <p>.
 *
 * @author wq li
 * @since 0.3.0
 */
public class R2dbcAuditMetadata {

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


    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Instant creationDate) {
        this.createdDate = creationDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}

