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

package top.leafage.common.data.r2dbc.domain;


import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;

import java.io.Serializable;

/**
 * Abstract base class for entities. Allows parameterization of id type, chooses auto-generation and implements
 * {@link #equals(Object)} and {@link #hashCode()} based on that id.
 *
 * @param <PK> the type of the identifier.
 * @author wq li
 */
public abstract class AbstractPersistable<PK extends Serializable> implements Persistable<PK> {

    @Nullable
    @Id
    private PK id;

    /**
     * {@inheritDoc}
     */
    @Override
    public PK getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Must be {@link Transient} in order to ensure that no provider complains because of a missing setter.
     *
     * @see Persistable#isNew()
     */
    @Transient
    @Override
    public boolean isNew() {
        return id == null;
    }


    /**
     * {@inheritDoc}
     * <p>
     * toString.
     */
    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * equals.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(ProxyUtils.getUserClass(obj))) {
            return false;
        }

        AbstractPersistable<?> that = (AbstractPersistable<?>) obj;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode += null == getId() ? 0 : getId().hashCode() * 31;
        return hashCode;
    }
}
