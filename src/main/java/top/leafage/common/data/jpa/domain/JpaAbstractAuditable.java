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

package top.leafage.common.data.jpa.domain;

import jakarta.persistence.MappedSuperclass;
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
 * @author wq li
 * @since 0.4.0
 */
@MappedSuperclass
public abstract class JpaAbstractAuditable<U, PK extends Serializable> extends JpaAbstractPersistable<PK>
        implements Auditable<U, PK, LocalDateTime> {

    private U createdBy;

    private Instant createdDate;

    private U lastModifiedBy;

    private Instant lastModifiedDate;


    @Override
    public Optional<U> getCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    @Override
    public void setCreatedBy(@Nullable U createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Optional<LocalDateTime> getCreatedDate() {
        return null == createdDate ? Optional.empty()
                : Optional.of(LocalDateTime.ofInstant(createdDate, ZoneId.systemDefault()));
    }

    @Override
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate.atZone(ZoneId.systemDefault()).toInstant();
    }

    @Override
    public Optional<U> getLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    @Override
    public void setLastModifiedBy(@Nullable U lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public Optional<LocalDateTime> getLastModifiedDate() {
        return null == lastModifiedDate ? Optional.empty()
                : Optional.of(LocalDateTime.ofInstant(lastModifiedDate, ZoneId.systemDefault()));
    }

    @Override
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate.atZone(ZoneId.systemDefault()).toInstant();
    }
}
