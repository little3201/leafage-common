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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive service interface for basic CRUD operations.
 *
 * @param <D> DTO type for input data
 * @param <V> VO type for output data
 * @author liwenqiang
 * @since 0.1.2
 */
public interface ReactiveBasicService<D, V> {

    /**
     * Retrieve all records.
     *
     * @return a Flux stream of type V
     */
    default Flux<V> retrieve() {
        return Flux.empty();
    }

    /**
     * Fetch a record by its ID.
     *
     * @param id the record ID
     * @return a Mono of type V, or an empty Mono if not found
     */
    default Mono<V> fetch(Long id) {
        return Mono.empty();
    }

    /**
     * Check if a record exists by its name.
     *
     * @param name the record name
     * @return a Mono emitting true if the record exists, false otherwise
     */
    default Mono<Boolean> exist(String name) {
        return Mono.empty();
    }

    /**
     * Create a new record.
     *
     * @param d the DTO representing the new record
     * @return a Mono of the created record of type V
     */
    default Mono<V> create(D d) {
        return Mono.empty();
    }

    /**
     * Modify an existing record by its ID.
     *
     * @param id the record ID
     * @param d  the DTO with updated data
     * @return a Mono of the updated record of type V
     */
    default Mono<V> modify(Long id, D d) {
        return Mono.empty();
    }

    /**
     * Remove a record by its ID.
     *
     * @param id the record ID
     * @return a Mono representing completion (Void)
     */
    default Mono<Void> remove(Long id) {
        return Mono.empty();
    }
}

