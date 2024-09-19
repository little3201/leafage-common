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
 * service interface.
 * D - DTO
 * V - VO
 *
 * @author liwenqiang 2021/7/20 23:14
 * @since 0.1.2
 **/
public interface ServletBasicService<D, V> {

    /**
     * retrieve.
     *
     * @return collect of the given type V
     */
    default List<V> retrieve() {
        return Collections.emptyList();
    }

    /**
     * fetch with given id.
     *
     * @param id row id
     * @return the given type V
     */
    default V fetch(Long id) {
        return null;
    }

    /**
     * is exist with given name.
     *
     * @param name row name
     * @return the result, if exist return true, else false
     */
    default boolean exist(String name) {
        return false;
    }

    /**
     * create a new row.
     *
     * @param d row
     * @return the given type V
     */
    default V create(D d) {
        return null;
    }

    /**
     * modify with given id and row.
     *
     * @param id row id
     * @param d  row
     * @return the given type V
     */
    default V modify(Long id, D d) {
        return null;
    }

    /**
     * remove with given id.
     *
     * @param id row id
     */
    default void remove(Long id) {
    }
}
