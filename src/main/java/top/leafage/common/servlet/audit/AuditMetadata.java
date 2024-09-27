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
package top.leafage.common.servlet.audit;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract class representing audit metadata for entities, providing fields and methods
 * for auditing the creation and modification details, such as the creator, modifier,
 * creation time, and modification time.
 * This class is intended to be extended by entity classes that require audit logging.
 * It leverages Spring Data's auditing infrastructure.
 * <p>
 * The {@code @MappedSuperclass} annotation allows this class's fields to be inherited by subclasses,
 * while the {@code @EntityListeners(AuditingEntityListener.class)} annotation enables audit event handling.
 *
 * @author wq li
 * @since 0.3.0
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditMetadata implements Auditable<String, Long, Instant> {

    /**
     * Primary key of the entity, automatically generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Indicates whether the entity is enabled. Default is {@code true}.
     */
    private boolean enabled = true;

    /**
     * The username of the user who created the entity. Not updatable after the entity is created.
     */
    @Column(name = "created_by", updatable = false, length = 50)
    private String createdBy;

    /**
     * The timestamp when the entity was created. Not updatable after the entity is created.
     */
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private Instant createdDate;

    /**
     * The username of the user who last modified the entity. Only updatable when the entity is modified.
     */
    @Column(name = "last_modified_by", insertable = false, length = 50)
    private String lastModifiedBy;

    /**
     * The timestamp when the entity was last modified. Only updatable when the entity is modified.
     */
    @LastModifiedDate
    @Column(name = "last_modified_date", insertable = false)
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

