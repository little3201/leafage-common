/*
 *  Copyright 2018-2023 the original author or authors.
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
 * service基础接口
 * D —— DTO
 * V —— VO
 *
 * @author liwenqiang 2021/7/20 23:14
 * @since 0.1.2
 **/
public interface ServletBasicService<D, V> {

    /**
     * 查询所有
     *
     * @return containing the elements of this list
     */
    default List<V> retrieve() {
        return Collections.emptyList();
    }

    /**
     * 获取
     *
     * @return an element instanceof vo
     */
    default V fetch(Long id) {
        return null;
    }

    /**
     * 是否存在
     *
     * @param name 名称
     * @return true-exist, false-not exist
     */
    default boolean exist(String name) {
        return false;
    }

    /**
     * 添加
     *
     * @param d 入参
     * @return a element instanceof vo
     */
    default V create(D d) {
        return null;
    }

    /**
     * 修改
     *
     * @param id 主键
     * @param d  入参
     * @return a element instanceof vo
     */
    default V modify(Long id, D d) {
        return null;
    }

    /**
     * 删除
     *
     * @param id 主键
     */
    default void remove(Long id) {
    }
}
