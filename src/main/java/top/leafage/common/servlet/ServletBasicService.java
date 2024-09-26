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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

/**
 * Servlet service interface for basic CRUD operations.
 *
 * @param <D> DTO type for input data
 * @param <V> VO type for output data
 * @since 0.1.2
 */
public interface ServletBasicService<D, V> {

    /**
     * Retrieves a paginated list of records.
     *
     * @param page       the page number
     * @param size       the number of records per page
     * @param sortBy     the field to sort by
     * @param descending whether sorting is in descending order
     * @return a paginated list of records
     * @since 0.3.0
     */
    default Page<V> retrieve(int page, int size, String sortBy, boolean descending) {
        Sort sort = descending ? Sort.by(Sort.Order.desc(sortBy)) : Sort.by(Sort.Order.asc(sortBy));
        Pageable pageable = PageRequest.of(page, size, sort);
        return Page.empty(pageable);
    }

    /**
     * Retrieves all records.
     *
     * @return a list of all records
     */
    default List<V> retrieve() {
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
     * Checks if a record exists by its name.
     *
     * @param name the record name
     * @return true if the record exists, false otherwise
     */
    default boolean exist(String name) {
        return false;
    }

    /**
     * Creates a new record.
     *
     * @param d the DTO representing the new record
     * @return the created record
     */
    default V create(D d) {
        return null;
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

}

