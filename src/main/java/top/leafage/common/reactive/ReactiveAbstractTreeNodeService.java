package top.leafage.common.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.basic.AbstractTreeNodeService;
import top.leafage.common.basic.TreeNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * For reactive to construct tree
 *
 * @author liwenqiang 2021-07-21 20:08
 * @since 0.1.3
 */
public abstract class ReactiveAbstractTreeNodeService<T> extends AbstractTreeNodeService<T> {

    /**
     * 处理子节点
     *
     * @param children 子节点
     * @return 树节点数据集
     * @since 0.1.9
     */
    protected Flux<TreeNode> convert(Flux<T> children) {
        return this.convert(children, null);
    }

    /**
     * 处理子节点
     *
     * @param children 子节点
     * @param expand   扩展属性
     * @return 树节点数据集
     * @since 0.1.9
     */
    protected Flux<TreeNode> convert(Flux<T> children, Set<String> expand) {
        Flux<TreeNode> nodesFlux = children.map(child -> this.construct(child, expand));
        Mono<Map<String, List<TreeNode>>> mapMono = nodesFlux.filter(node -> Objects.nonNull(node.getSuperior()) &&
                        !"0".equals(node.getSuperior()))
                .collect(Collectors.groupingBy(TreeNode::getSuperior));

        return nodesFlux.zipWith(mapMono, (node, map) -> {
            node.setChildren(map.get(node.getCode()));
            return node;
        }).filter(node -> Objects.isNull(node.getSuperior()) || "0".equals(node.getSuperior()));
    }

}
