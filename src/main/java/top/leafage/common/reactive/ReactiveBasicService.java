package top.leafage.common.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
public interface ReactiveBasicService<D, V, C> {

    /**
     * 获取所有entities
     *
     * @return a flux containing the elements of this list
     */
    default Flux<V> retrieve() {
        return Flux.empty();
    }

    /**
     * 根据code查询
     *
     * @param code 代码
     * @return a element instanceof vo
     */
    default Mono<V> fetch(C code) {
        return Mono.empty();
    }

    /**
     * 是否存在
     *
     * @param param 属性
     * @return true-exist, false-not exist
     */
    default Mono<Boolean> exist(String param) {
        return Mono.empty();
    }

    /**
     * 删除
     *
     * @param code 代码
     * @return Void no return
     */
    default Mono<Void> remove(C code) {
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
     * 添加
     *
     * @param d 实例
     * @return a element instanceof vo
     */
    default Mono<V> create(D d) {
        return Mono.empty();
    }

    /**
     * 修改
     *
     * @param code 代码
     * @param d    实例
     * @return a element instanceof vo
     */
    default Mono<V> modify(C code, D d) {
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
