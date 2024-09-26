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

package top.leafage.common.reactive;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive service interface for basic CRUD operations.
 *
 * @param <D> DTO type for input data
 * @param <V> VO type for output data
 * @since 0.1.2
 */
public interface ReactiveBasicService<D, V> {

    /**
     * Retrieves a paginated list of records.
     *
     * @param page       the page number
     * @param size       the number of records per page
     * @param sortBy     the field to sort by
     * @param descending whether sorting is in descending order
     * @return a Mono containing a paginated list of records
     * @since 0.3.0
     */
    default Mono<Page<V>> retrieve(int page, int size, String sortBy, boolean descending) {
        Sort sort = descending ? Sort.by(Sort.Order.desc(sortBy)) : Sort.by(Sort.Order.asc(sortBy));
        Pageable pageable = PageRequest.of(page, size, sort);
        return Mono.just(Page.empty(pageable));
    }

    /**
     * Retrieves all records.
     *
     * @return a Flux stream of all records
     */
    default Flux<V> retrieve() {
        return Flux.empty();
    }

    /**
     * Fetches a record by its ID.
     *
     * @param id the record ID
     * @return a Mono containing the record, or an empty Mono if not found
     */
    default Mono<V> fetch(Long id) {
        return Mono.empty();
    }

    /**
     * Checks if a record exists by its name.
     *
     * @param name the record name
     * @return a Mono emitting true if the record exists, false otherwise
     */
    default Mono<Boolean> exist(String name) {
        return Mono.empty();
    }

    /**
     * Creates a new record.
     *
     * @param d the DTO representing the new record
     * @return a Mono containing the created record
     */
    default Mono<V> create(D d) {
        return Mono.empty();
    }

    /**
     * Updates an existing record by its ID.
     *
     * @param id the record ID
     * @param d  the DTO containing updated data
     * @return a Mono containing the updated record
     */
    default Mono<V> modify(Long id, D d) {
        return Mono.empty();
    }

    /**
     * Removes a record by its ID.
     *
     * @param id the record ID
     * @return a Mono indicating completion
     */
    default Mono<Void> remove(Long id) {
        return Mono.empty();
    }
}


