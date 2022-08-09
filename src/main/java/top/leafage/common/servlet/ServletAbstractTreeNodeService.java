package top.leafage.common.servlet;

import top.leafage.common.basic.AbstractTreeNodeService;
import top.leafage.common.basic.TreeNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * For servlet to construct tree
 *
 * @author liwenqiang 2021-07-21 20:08
 * @since 0.1.3
 */
public abstract class ServletAbstractTreeNodeService<T> extends AbstractTreeNodeService<T> {

    /**
     * 处理子节点
     *
     * @param children 子节点
     * @return 树节点数据集
     * @since 0.1.9
     */
    protected List<TreeNode> convert(List<T> children) {
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
    protected List<TreeNode> convert(List<T> children, Set<String> expand) {
        Stream<TreeNode> stream = children.stream().map(child -> this.construct(child, expand));
        Map<String, List<TreeNode>> listMap = stream.filter(node -> Objects.nonNull(node.getSuperior()) &&
                        !"0".equals(node.getSuperior()))
                .collect(Collectors.groupingBy(TreeNode::getSuperior));

        stream.forEach(node -> node.setChildren(listMap.get(node.getCode())));
        return stream.filter(node -> Objects.isNull(node.getSuperior()) || "0".equals(node.getSuperior()))
                .collect(Collectors.toList());
    }

}
