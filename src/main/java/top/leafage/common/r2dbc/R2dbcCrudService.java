/*
 *  Copyright 2018-2025 little3201.
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

package top.leafage.common.r2dbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * Reactive service interface for basic CRUD operations.
 *
 * @param <D> DTO type for input data
 * @param <V> VO type for output data
 * @author wq li
 * @since 0.3.4
 */
public interface R2dbcCrudService<D, V> {

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
    default Mono<Page<V>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        return Mono.just(new PageImpl<>(Collections.emptyList()));
    }

    /**
     * Retrieves all records or by given ids.
     *
     * @param ids the given records id .
     * @return a Flux stream of all records.
     */
    default Flux<V> retrieve(List<Long> ids) {
        return Flux.empty();
    }

    /**
     * Fetches a record by its ID.
     *
     * @param id the record ID.
     * @return a Mono containing the record, or an empty Mono if not found.
     */
    default Mono<V> fetch(Long id) {
        return Mono.empty();
    }

    /**
     * Checks if a record exists by it's field.
     *
     * @param field the record's field
     * @param id    the record's id
     * @return a Mono emitting true if the record exists, false otherwise.
     */
    default Mono<Boolean> exists(String field, Long id) {
        return Mono.just(false);
    }

    /**
     * Enable or Disable a record by it's ID.
     *
     * @param id the record ID
     * @return a Mono emitting true if the record enabled/disabled, false otherwise
     */
    default Mono<Boolean> enable(Long id) {
        return Mono.just(false);
    }

    /**
     * Creates a new record.
     *
     * @param d the DTO representing the new record.
     * @return a Mono containing the created record.
     */
    default Mono<V> create(D d) {
        return Mono.empty();
    }

    /**
     * Creates all given records.
     *
     * @param iterable the new records.
     * @return the created records.
     */
    default Flux<V> createAll(Iterable<D> iterable) {
        return Flux.empty();
    }

    /**
     * Updates an existing record by its ID.
     *
     * @param id the record ID.
     * @param d  the DTO containing updated data.
     * @return a Mono containing the updated record.
     */
    default Mono<V> modify(Long id, D d) {
        return Mono.empty();
    }

    /**
     * Removes a record by its ID.
     *
     * @param id the record ID.
     * @return a Mono indicating completion.
     */
    default Mono<Void> remove(Long id) {
        return Mono.empty();
    }
}


