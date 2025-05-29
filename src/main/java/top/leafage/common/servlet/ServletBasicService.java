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

package top.leafage.common.servlet;

import top.leafage.common.BasicService;
import top.leafage.common.ReadonlyMetadata;
import top.leafage.common.servlet.audit.AuditMetadata;

import java.util.Collections;
import java.util.List;

/**
 * Servlet service interface for basic CRUD operations.
 *
 * @param <D> DTO type for input data
 * @param <V> VO type for output data
 * @since 0.1.2
 */
public interface ServletBasicService<D, V> extends BasicService {

    /**
     * Retrieves all records.
     *
     * @param ids the given records id .
     * @return a list of all records
     */
    default List<V> retrieve(List<Long> ids) {
        return Collections.emptyList();
    }

    /**
     * Fetches a record by its ID.
     *
     * @param id the record ID
     * @return the record, or null if not found
     */
    default V fetch(Long id) {
        return null;
    }

    /**
     * Checks if a record exists by it's field.
     *
     * @param field the record's field
     * @param id    the record's id
     * @return true if the record exists, false otherwise
     */
    default boolean exists(String field, Long id) {
        return false;
    }

    /**
     * Enable or Disable a record by it's ID.
     *
     * @param id the record ID
     * @return true if the record enabled/disabled, false otherwise
     */
    default boolean enable(Long id) {
        return false;
    }

    /**
     * Creates a new record.
     *
     * @param d the new record
     * @return the created record
     */
    default V create(D d) {
        return null;
    }

    /**
     * Creates all given records.
     *
     * @param iterable the new records.
     * @return the created records.
     */
    default List<V> createAll(Iterable<D> iterable) {
        return Collections.emptyList();
    }

    /**
     * Updates an existing record by its ID.
     *
     * @param id the record ID
     * @param d  the DTO containing updated data
     * @return the updated record
     */
    default V modify(Long id, D d) {
        return null;
    }

    /**
     * Removes a record by its ID.
     *
     * @param id the record ID
     */
    default void remove(Long id) {
    }

    /**
     * Converts a source object to an instance of the target class.
     *
     * @param source  The source object to convert.
     * @param voClass The class of the vo object.
     * @param <S>     The type of the source object.
     * @param <T>     The type of the target object.
     * @return An instance of the target class.
     * @throws RuntimeException if the conversion fails.
     */
    default <S extends AuditMetadata, T extends ReadonlyMetadata> T convertToVO(S source, Class<T> voClass) {
        try {
            T target = create(source.getId(), source.isEnabled(),
                    source.getLastModifiedDate().orElse(null), voClass);
            return convert(source, target);
        } catch (Exception e) {
            throw new RuntimeException("Convert to vo error", e);
        }
    }

    /**
     * Converts a source object to an instance of the target class.
     *
     * @param source      The source object to convert.
     * @param targetClass The class of the target object.
     * @param <S>         The type of the source object.
     * @param <T>         The type of the target object.
     * @return An instance of the target class.
     * @throws RuntimeException if the conversion fails.
     */
    default <S, T extends AuditMetadata> T convertToDomain(S source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            return convert(source, target);
        } catch (Exception e) {
            throw new RuntimeException("Convert to domain error", e);
        }
    }

}

