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

package top.leafage.common.servlet;

import java.util.Collections;
import java.util.List;

/**
 * Service interface providing basic CRUD operations.
 *
 * @param <D> DTO type for data input
 * @param <V> VO type for data output
 * @author wq li
 * @since 0.1.2
 */
public interface ServletBasicService<D, V> {

    /**
     * Retrieve all records.
     *
     * @return a list of type V
     */
    default List<V> retrieve() {
        return Collections.emptyList();
    }

    /**
     * Fetch a record by its ID.
     *
     * @param id the record ID
     * @return the record of type V, or null if not found
     */
    default V fetch(Long id) {
        return null;
    }

    /**
     * Check if a record exists by its name.
     *
     * @param name the record name
     * @return true if the record exists, false otherwise
     */
    default boolean exist(String name) {
        return false;
    }

    /**
     * Create a new record.
     *
     * @param d the DTO representing the new record
     * @return the created record of type V
     */
    default V create(D d) {
        return null;
    }

    /**
     * Modify an existing record by its ID.
     *
     * @param id the record ID
     * @param d  the DTO with updated data
     * @return the updated record of type V
     */
    default V modify(Long id, D d) {
        return null;
    }

    /**
     * Remove a record by its ID.
     *
     * @param id the record ID
     */
    default void remove(Long id) {
    }
}

