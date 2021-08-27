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
 *
 * @author liwenqiang 2018/7/27 23:14
 **/
public interface BasicService<D, V> {

    /**
     * 查询
     *
     * @return an array containing the elements of this list
     */
    default List<V> retrieve() {
        return Collections.emptyList();
    }

    /**
     * 根据唯一标识获取
     *
     * @param code 代码
     * @return a element instanceof vo
     */
    default V fetch(Object code) {
        return null;
    }

    /**
     * 名称是否存在
     *
     * @param name 名称
     * @return true-exist, false-not exist
     */
    default boolean exist(String name) {
        return false;
    }

    /**
     * 根据唯一标识删除entity
     *
     * @param code 代码
     */
    default void remove(Object code) {
    }

    /**
     * 批量删除
     *
     * @param dList 实例集合
     */
    default void removeAll(List<D> dList) {
    }

    /**
     * 添加对象
     *
     * @param d 入参
     * @return a element instanceof vo
     */
    default V create(D d) {
        return null;
    }

    /**
     * 修改对象
     *
     * @param code 唯一标识
     * @param d    入参
     * @return a element instanceof vo
     */
    default V modify(Object code, D d) {
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
