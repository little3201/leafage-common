/*
 *  Copyright 2018-2024 little3201.
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
package top.leafage.common.reactive.audit;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Auditable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract class representing audit metadata for reactive entities.
 * Provides fields and methods for managing audit information such as entity creation,
 * modification details, and whether the entity is enabled. Intended to be extended
 * by entity classes that require audit tracking in a reactive context.
 * <p>
 * Annotations like {@code @InsertOnlyProperty} and {@code @CreatedDate} handle
 * database insertion and automatic timestamping for creation events.
 * <p>
 * This class implements the {@link Auditable} interface for consistent audit behavior.
 *
 * @author wq li
 * @since 0.3.0
 */
public abstract class ReactiveAuditMetadata implements Auditable<String, Long, Instant> {

    /**
     * The primary key of the entity.
     */
    @Id
    private Long id;

    /**
     * Indicates whether the entity is enabled. Defaults to {@code true}.
     */
    @Column(value = "is_enabled")
    private boolean enabled = true;

    /**
     * The username of the user who created the entity. Immutable once set.
     */
    @InsertOnlyProperty
    @Column(value = "created_by")
    private String createdBy;

    /**
     * The timestamp when the entity was created. Automatically set on insertion.
     */
    @InsertOnlyProperty
    @CreatedDate
    @Column(value = "created_date")
    private Instant createdDate;

    /**
     * The username of the user who last modified the entity.
     */
    @Column(value = "last_modified_by")
    private String lastModifiedBy;

    /**
     * The timestamp when the entity was last modified. Automatically updated on modification.
     */
    @LastModifiedDate
    @Column(value = "last_modified_date")
    private Instant lastModifiedDate;

    /**
     * Returns whether the entity is enabled.
     *
     * @return {@code true} if the entity is enabled, otherwise {@code false}.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled status of the entity.
     *
     * @param enabled a boolean indicating the enabled status.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getCreatedBy() {
        return Optional.ofNullable(this.createdBy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Instant> getCreatedDate() {
        return Optional.ofNullable(this.createdDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreatedDate(Instant creationDate) {
        this.createdDate = creationDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getLastModifiedBy() {
        return Optional.ofNullable(this.lastModifiedBy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Instant> getLastModifiedDate() {
        return Optional.ofNullable(this.lastModifiedDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the primary key of the entity.
     *
     * @param id a {@link Long} representing the entity ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNew() {
        return Objects.isNull(getId());
    }
}

