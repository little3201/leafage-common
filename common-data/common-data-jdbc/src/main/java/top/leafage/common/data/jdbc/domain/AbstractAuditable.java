/*
 * Copyright (c) 2025-2026.  little3201.
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
package top.leafage.common.data.jdbc.domain;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Auditable;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Abstract base class for auditable entities. Stores the audition values in persistent fields.
 *
 * @param <U>  the type of the Entity.
 * @param <PK> the type of the identifier.
 * @author wq li
 */
public abstract class AbstractAuditable<U, PK extends Serializable> extends AbstractPersistable<PK>
        implements Auditable<U, PK, LocalDateTime> {

    private U createdBy;

    private Instant createdDate;

    private U lastModifiedBy;

    private Instant lastModifiedDate;


    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<U> getCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreatedBy(@Nullable U createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LocalDateTime> getCreatedDate() {
        return null == createdDate ? Optional.empty()
                : Optional.of(LocalDateTime.ofInstant(createdDate, ZoneId.systemDefault()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<U> getLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastModifiedBy(@Nullable U lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LocalDateTime> getLastModifiedDate() {
        return null == lastModifiedDate ? Optional.empty()
                : Optional.of(LocalDateTime.ofInstant(lastModifiedDate, ZoneId.systemDefault()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate.atZone(ZoneId.systemDefault()).toInstant();
    }
}

