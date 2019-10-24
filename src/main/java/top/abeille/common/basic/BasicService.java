/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.basic;

import org.springframework.data.domain.ExampleMatcher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * service基础接口
 *
 * @author liwenqiang 2018/7/27 23:14
 **/
public interface BasicService<D, V> {
    /**
     * 根据id获取entity
     *
     * @param id 业务主键
     * @return T
     */
    default Mono<V> queryById(Long id) {
        return Mono.empty();
    }

    /**
     * 获取所有entities并排序
     *
     * @return List<T>
     */
    default Flux<V> fetchAll() {
        return Flux.empty();
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
    default Flux<V> fetchByExample(D d, ExampleMatcher exampleMatcher) {
        return Flux.empty();
    }

    /**
     * 获取行
     *
     * @return long type result
     */
    default Mono<Long> queryCount() {
        return Mono.empty();
    }

    /**
     * 根据主键ID删除entity
     *
     * @param id 业务主键
     */
    default Mono<Void> removeById(Long id) {
        return Mono.empty();
    }

    /**
     * 批量删除
     *
     * @param dList 实例集合
     * @return 删除结果
     */
    default Mono<Void> removeInBatch(List<D> dList) {
        return Mono.empty();
    }

    /**
     * 保存entity
     *
     * @param id 业务主键
     * @param s  实例
     * @return 保存结果
     */
    default Mono<V> save(Long id, D s) {
        return Mono.empty();
    }

    /**
     * 批量保存
     *
     * @param dList 实例集合
     * @return 实例类型
     */
    default Flux<V> saveInBatch(List<D> dList) {
        return Flux.empty();
    }
}
