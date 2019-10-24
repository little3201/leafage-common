/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.basic;

import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
     * 获取所有entities并排序
     *
     * @param sort 排序
     * @return List<V>
     */
    default List<V> fetchAll(Sort sort) {
        return Collections.emptyList();
    }

    /**
     * 根据条件查询所有——设置匹配条件，如例所示：
     * Type one: ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnorePaths("oneVar","twoVar");
     * Type two: ExampleMatcher exampleMatcher = ExampleMatcher.matching()
     * .withMatcher(roleInfoModel.getRoleName(), startsWith().ignoreCase())
     * .withMatcher(String.valueOf(roleInfoModel.getRoleId()), ExampleMatcher.GenericPropertyMatchers.contains());
     *
     * @param d              实例
     * @param exampleMatcher 匹配条件
     * @return List<T>
     */
    default List<V> fetchAllByExample(D d, ExampleMatcher exampleMatcher) {
        return Collections.emptyList();
    }

    /**
     * 分页获取所有entities
     *
     * @param pageable 分页参数
     * @return Page<T>
     */
    default Page<V> fetchAllByPage(Pageable pageable) {
        return null;
    }

    /**
     * 根据业务id获取
     *
     * @param businessId 业务主键
     * @return T
     */
    default V queryById(Long businessId) {
        return null;
    }

    /**
     * 获取行
     *
     * @return long type result
     */
    default long queryCount() {
        return 0;
    }


    /**
     * 根据主键ID删除entity
     *
     * @param id 主键ID
     */
    void removeById(Long id);

    /**
     * 批量删除
     *
     * @param dList 实例集合
     */
    void removeInBatch(List<D> dList);

    /**
     * 保存对象
     *
     * @param d 入参
     * @return T
     */
    default V save(D d) {
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
