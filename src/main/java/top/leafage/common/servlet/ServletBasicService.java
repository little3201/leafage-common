/*
 * Copyright (c) 2019. Abeille All Right Reserved.
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
     * @return an array containing the elements of this list
     */
    default List<V> saveAll(List<D> dList) {
        return Collections.emptyList();
    }
}
