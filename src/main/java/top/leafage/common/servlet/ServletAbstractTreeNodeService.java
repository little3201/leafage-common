package top.leafage.common.servlet;

import top.leafage.common.basic.AbstractTreeNodeService;
import top.leafage.common.basic.TreeNode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
     * @param superior 上级数据
     * @param children 子节点
     * @return 树节点数据集
     */
    protected List<TreeNode> children(T superior, List<T> children) {
        return this.children(superior, children, null);
    }

    /**
     * 处理子节点
     *
     * @param superior 上级数据
     * @param children 子节点
     * @param expand   扩展属性
     * @return 树节点数据集
     */
    protected List<TreeNode> children(T superior, List<T> children, Set<String> expand) {
        Class<?> aClass = superior.getClass();
        Object superiorId = this.getId(superior, aClass);
        Object superiorCode = this.getCode(superior, aClass);

        return children.stream().filter(child -> this.check(superiorId, child)).map(child -> {
            TreeNode treeNode = this.construct(superiorCode, child, expand);

            treeNode.setChildren(this.children(child, children, expand));

            return treeNode;
        }).collect(Collectors.toList());
    }

}
