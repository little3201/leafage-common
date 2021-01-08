/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.basic;

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
     * 分页查询
     *
     * @param page 页码
     * @param size 大小
     * @return List<V>
     */
    default List<V> retrieve(int page, int size) {
        return Collections.emptyList();
    }

    /**
     * 分页查询并排序
     *
     * @param page  页码
     * @param size  大小
     * @param order 排序
     * @return List<V>
     */
    default List<V> retrieve(int page, int size, String order) {
        return Collections.emptyList();
    }

    /**
     * 根据唯一标识获取
     *
     * @param code 代码
     * @return T
     */
    default V fetch(String code) {
        return null;
    }

    /**
     * 根据唯一标识删除entity
     *
     * @param code 代码
     */
    default void remove(String code) {
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
     * @return T
     */
    default V create(D d) {
        return null;
    }

    /**
     * 修改对象
     *
     * @param code 唯一标识
     * @param d    入参
     * @return T
     */
    default V modify(String code, D d) {
        return null;
    }

    /**
     * 批量保存
     *
     * @param dList 实例集合
     * @return 实例类型
     */
    default List<V> saveAll(List<D> dList) {
        return Collections.emptyList();
    }
}
