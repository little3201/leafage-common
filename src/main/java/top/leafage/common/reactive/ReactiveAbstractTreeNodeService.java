package top.leafage.common.reactive;

import reactor.core.publisher.Flux;
import top.leafage.common.basic.AbstractTreeNodeService;
import top.leafage.common.basic.TreeNode;
import java.util.Set;

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
     * @param superior 上级数据
     * @param children 子节点
     * @return 树节点数据集
     */
    protected Flux<TreeNode> children(T superior, Flux<T> children) {
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
    protected Flux<TreeNode> children(T superior, Flux<T> children, Set<String> expand) {
        Class<?> aClass = superior.getClass();
        // ID是集成基础父类的，所以要通过superClass获取
        Object superiorId = this.getId(superior, aClass);
        Object superiorName = this.getName(superior, aClass);

        return children.filter(child -> this.check(superiorId, child)).flatMap(child -> {
            Class<?> childClass = child.getClass();
            Object code = this.getCode(child, childClass);
            Object name = this.getName(child, childClass);

            TreeNode treeNode = new TreeNode(code != null ? code.toString() : null,
                    name != null ? name.toString() : null);
            treeNode.setSuperior(superiorName != null ? superiorName.toString() : null);

            // deal expand
            this.expand(treeNode, childClass, child, expand);

            return this.children(child, children, expand).collectList().map(treeNodes -> {
                treeNode.setChildren(treeNodes);
                return treeNode;
            });
        });
    }

}
