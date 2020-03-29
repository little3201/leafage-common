/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.basic;

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
     * 获取所有entities
     *
     * @return List<T>
     */
    default Flux<V> retrieveAll() {
        return Flux.empty();
    }

    /**
     * 根据id获取entity
     *
     * @param businessId 业务主键
     * @return T
     */
    default Mono<V> fetchByBusinessId(String businessId) {
        return Mono.empty();
    }

    /**
     * 根据主键ID删除entity
     *
     * @param businessId 业务主键
     */
    default Mono<Void> removeById(String businessId) {
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
     * 新增entity
     *
     * @param d 实例
     * @return 保存结果
     */
    default Mono<V> create(D d) {
        return Mono.empty();
    }

    /**
     * 编辑entity
     *
     * @param businessId 业务主键
     * @param d          实例
     * @return 保存结果
     */
    default Mono<V> modify(String businessId, D d) {
        return Mono.empty();
    }

    /**
     * 批量保存
     *
     * @param dList 实例集合
     * @return 实例类型
     */
    default Flux<V> saveAll(List<D> dList) {
        return Flux.empty();
    }
}
