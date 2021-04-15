package io.leafage.common.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReactiveBasicService<D, V> {

    /**
     * 获取所有entities
     *
     * @return List<T>
     */
    default Flux<V> retrieve() {
        return Flux.empty();
    }

    /**
     * 分页获取entities
     *
     * @return List<T>
     */
    default Flux<V> retrieve(int page, int size) {
        return Flux.empty();
    }

    /**
     * 分页获取排序后的entities
     *
     * @return List<T>
     */
    default Flux<V> retrieve(int page, int size, String order) {
        return Flux.empty();
    }

    /**
     * 根据code获取entity
     *
     * @param code 代码
     * @return T
     */
    default Mono<V> fetch(String code) {
        return Mono.empty();
    }

    /**
     * 统计记录数
     *
     * @return T
     */
    default Mono<Long> count() {
        return Mono.empty();
    }

    /**
     * 根据code删除entity
     *
     * @param code 代码
     */
    default Mono<Void> remove(String code) {
        return Mono.empty();
    }

    /**
     * 批量删除
     *
     * @param dList 实例集合
     * @return 删除结果
     */
    default Mono<Void> removeAll(List<D> dList) {
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
     * @param code 代码
     * @param d    实例
     * @return 保存结果
     */
    default Mono<V> modify(String code, D d) {
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
