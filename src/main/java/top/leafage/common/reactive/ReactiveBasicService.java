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
 * service基础接口
 * D —— DTO
 * V —— VO
 *
 * @author liwenqiang 2021/7/20 23:14
 * @since 0.1.2
 **/
public interface ReactiveBasicService<D, V> {

    /**
     * 获取所有
     *
     * @return containing the elements of this list
     */
    default Flux<V> retrieve() {
        return Flux.empty();
    }

    /**
     * 获取
     *
     * @return an element instanceof vo
     */
    default Mono<V> fetch(Long id) {
        return Mono.empty();
    }

    /**
     * 是否存在
     *
     * @param name 属性
     * @return true-exist, false-not exist
     */
    default Mono<Boolean> exist(String name) {
        return Mono.empty();
    }

    /**
     * 添加
     *
     * @param d 实例
     * @return a element instanceof vo
     */
    default Mono<V> create(D d) {
        return Mono.empty();
    }

    /**
     * 修改
     *
     * @param id 主键
     * @param d  实例
     * @return a element instanceof vo
     */
    default Mono<V> modify(Long id, D d) {
        return Mono.empty();
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return Void
     */
    default Mono<Void> remove(Long id) {
        return Mono.empty();
    }
}
