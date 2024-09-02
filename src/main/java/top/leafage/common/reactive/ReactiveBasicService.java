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
 * service interface.
 * D —— DTO
 * V —— VO
 *
 * @author liwenqiang 2021/7/20 23:14
 * @since 0.1.2
 **/
public interface ReactiveBasicService<D, V> {

    /**
     * retrieve.
     *
     * @return collect of the given type V
     */
    default Flux<V> retrieve() {
        return Flux.empty();
    }

    /**
     * fetch with given id.
     *
     * @param id row id
     * @return the given type V
     */
    default Mono<V> fetch(Long id) {
        return Mono.empty();
    }

    /**
     * is exist with given name.
     *
     * @param name row name
     * @return the result, if exist return true, else false
     */
    default Mono<Boolean> exist(String name) {
        return Mono.empty();
    }

    /**
     * create a new row.
     *
     * @param d row
     * @return the given type V
     */
    default Mono<V> create(D d) {
        return Mono.empty();
    }

    /**
     * modify with given id and row.
     *
     * @param id row id
     * @param d  row
     * @return the given type V
     */
    default Mono<V> modify(Long id, D d) {
        return Mono.empty();
    }

    /**
     * remove with given id.
     *
     * @param id row id
     * @return Void
     */
    default Mono<Void> remove(Long id) {
        return Mono.empty();
    }
}
