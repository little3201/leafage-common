/*
 *  Copyright 2018-2022 the original author or authors.
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
 * C —— code type
 *
 * @author liwenqiang 2021/7/20 23:14
 * @since 0.1.2
 **/
public interface ServletBasicService<D, V, C> {

    /**
     * 查询
     *
     * @return an array containing the elements of this list
     */
    default List<V> retrieve() {
        return Collections.emptyList();
    }

    /**
     * 根据code查询
     *
     * @param code 代码
     * @return a element instanceof vo
     */
    default V fetch(C code) {
        return null;
    }

    /**
     * 是否存在
     *
     * @param param 名称
     * @return true-exist, false-not exist
     */
    default boolean exist(String param) {
        return false;
    }

    /**
     * 删除
     *
     * @param code 代码
     */
    default void remove(C code) {
    }

    /**
     * 批量删除
     *
     * @param dList 实例集合
     */
    default void removeAll(List<D> dList) {
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
     * @param code 唯一标识
     * @param d    入参
     * @return a element instanceof vo
     */
    default V modify(C code, D d) {
        return null;
    }

    /**
     * 批量保存
     *
     * @param dList 实例集合
     * @return an list containing the elements of this list
     */
    default List<V> saveAll(List<D> dList) {
        return Collections.emptyList();
    }
}
