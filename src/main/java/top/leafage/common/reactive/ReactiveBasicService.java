package top.leafage.common.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

public interface ReactiveBasicService<D, V> {

    /**
     * 获取所有entities
     *
     * @return a flux containing the elements of this list
     */
    default Flux<V> retrieve() {
        return Flux.empty();
    }

    /**
     * 分页获取entities
     *
     * @param page 页码
     * @param size 分页大小
     * @return a flux containing the elements of this list
     */
    default Flux<V> retrieve(int page, int size) {
        return Flux.empty();
    }

    /**
     * 分页获取排序后的entities
     *
     * @param page  页码
     * @param size  分页大小
     * @param sort 排序字段
     * @return a flux containing the elements of this list
     */
    default Flux<V> retrieve(int page, int size, String sort) {
        return Flux.empty();
    }

    /**
     * 名称是否存在
     *
     * @param name 名称
     * @return true-exist, false-not exist
     */
    default Mono<Boolean> exist(String name) {
        return Mono.empty();
    }

    /**
     * 根据code获取entity
     *
     * @param code 代码
     * @return a element instanceof vo
     */
    default Mono<V> fetch(Object code) {
        return Mono.empty();
    }

    /**
     * 统计记录数
     *
     * @return the count of elements
     */
    default Mono<Long> count() {
        return Mono.empty();
    }

    /**
     * 根据code删除entity
     *
     * @param code 代码
     * @return Void no return
     */
    default Mono<Void> remove(Object code) {
        return Mono.empty().then();
    }

    /**
     * 批量删除
     *
     * @param dList 实例集合
     * @return Void no return
     */
    default Mono<Void> removeAll(List<D> dList) {
        return Mono.empty().then();
    }

    /**
     * 新增entity
     *
     * @param d 实例
     * @return a element instanceof vo
     */
    default Mono<V> create(D d) {
        return Mono.empty();
    }

    /**
     * 编辑entity
     *
     * @param code 代码
     * @param d    实例
     * @return a element instanceof vo
     */
    default Mono<V> modify(Object code, D d) {
        return Mono.empty();
    }

    /**
     * 批量保存
     *
     * @param dList 实例集合
     * @return a flux containing the elements of this list
     */
    default Flux<V> saveAll(List<D> dList) {
        return Flux.empty();
    }
}
