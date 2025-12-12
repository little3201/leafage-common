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

package top.leafage.common.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;

/**
 * Servlet service interface.
 *
 * @param <D> The dto type.
 * @param <V> the vo type.
 * @author wq li
 * @since 0.3.7
 */
public interface CrudService<D, V> extends Service {

    /**
     * Retrieves records by pageable, sort, filters.
     *
     * @param page       The page number (zero-based).
     * @param size       The size of the page (number of items per page), capped at 500.
     * @param sortBy     The field to sort by, or null for unsorted pagination.
     * @param descending Whether the sorting should be in descending order.
     * @param filters    filters to apply to the query.
     * @return a Flux stream of all records.
     */
    default Page<V> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        return new PageImpl<>(Collections.emptyList());
    }

    /**
     * Retrieves all records or by given pks.
     *
     * @param ids the given records id .
     * @return a list of all records
     */
    default List<V> retrieve(List<Long> ids) {
        return Collections.emptyList();
    }

    /**
     * Fetch a record by pk.
     *
     * @param id the pk.
     * @return the record, or null if not found
     */
    default V fetch(Long id) {
        return null;
    }

    /**
     * Enable or Disable a record by pk.
     *
     * @param id the pk.
     * @return true if the record enabled/disabled, false otherwise
     */
    default boolean enable(Long id) {
        return false;
    }

    /**
     * Creates a new record.
     *
     * @param dto the dto.
     * @return the created record
     */
    default V create(D dto) {
        return null;
    }

    /**
     * Creates all given records.
     *
     * @param iterable the dto iterable.
     * @return the created records.
     */
    default List<V> createAll(Iterable<D> iterable) {
        return Collections.emptyList();
    }

    /**
     * Updates an existing record by pk.
     *
     * @param id  the pk.
     * @param dto the dto.
     * @return the updated record
     */
    default V modify(Long id, D dto) {
        return null;
    }

    /**
     * Removes a record by pk.
     *
     * @param id the pk.
     */
    default void remove(Long id) {
    }
}

