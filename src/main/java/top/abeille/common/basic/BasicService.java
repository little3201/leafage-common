/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.basic;

import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * service基础接口
 *
 * @author liwenqiang 2018/7/27 23:14
 **/
public interface BasicService<S, T> {
    /**
     * 根据id获取entity
     *
     * @param id 主键
     * @return T
     */
    default Mono<T> getById(String id) {
        return Mono.empty();
    }

    /**
     * 根据条件模版获取entity
     *
     * @param s 实例
     * @return T
     */
    default Mono<T> getByExample(S s) {
        return Mono.empty();
    }

    /**
     * 获取所有的entity
     *
     * @return List<T>
     */
    default Flux<T> findAll() {
        return Flux.empty();
    }

    /**
     * 获取所有entities并排序
     *
     * @param sort 排序
     * @return List<T>
     */
    default Flux<T> findAll(Sort sort) {
        return Flux.empty();
    }

    /**
     * 根据条件查询所有——设置匹配条件，如例所示：
     * Type one: ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnorePaths("oneVar","twoVar");
     * Type two: ExampleMatcher exampleMatcher = ExampleMatcher.matching()
     * .withMatcher(roleInfoModel.getRoleName(), startsWith().ignoreCase())
     * .withMatcher(String.valueOf(roleInfoModel.getRoleId()), ExampleMatcher.GenericPropertyMatchers.contains());
     *
     * @param s              实例
     * @param exampleMatcher 匹配条件
     * @return List<T>
     */
    default Flux<T> findAllByExample(S s, ExampleMatcher exampleMatcher) {
        return Flux.empty();
    }

    /**
     * 获取行
     *
     * @return long type result
     */
    default Mono<Long> getCount() {
        return Mono.empty();
    }

    /**
     * 根据主键ID删除entity
     *
     * @param id 主键ID
     */
    default Mono<Void> removeById(String id) {
        return Mono.empty();
    }

    /**
     * 批量删除
     *
     * @param entities 实例集合
     * @return 删除结果
     */
    default Mono<Void> removeInBatch(List<T> entities) {
        return Mono.empty();
    }

    /**
     * 保存entity
     *
     * @param s 实例
     * @return 保存结果
     */
    default Mono<T> save(S s) {
        return Mono.empty();
    }

    /**
     * 批量保存
     *
     * @param entities 实例集合
     * @return 实例类型
     */
    default Flux<T> saveAll(List<S> entities) {
        return Flux.empty();
    }
}
