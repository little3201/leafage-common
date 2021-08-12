package top.leafage.common.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.basic.AbstractTreeNodeService;
import top.leafage.common.basic.TreeNode;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

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
        try {
            Object superiorId = aClass.getSuperclass().getMethod("getId").invoke(superior);
            Object superiorName = aClass.getMethod("getName").invoke(superior);
            return children.filter(child -> this.check(superiorId, child)).flatMap(child -> {
                Class<?> childClass = child.getClass();
                try {
                    Object code = childClass.getMethod("getCode").invoke(child);
                    Object name = childClass.getMethod("getName").invoke(child);

                    TreeNode treeNode = new TreeNode(code != null ? code.toString() : null,
                            name != null ? name.toString() : null);
                    treeNode.setSuperior(superiorName != null ? superiorName.toString() : null);

                    // deal expand
                    this.expand(treeNode, childClass, child, expand);

                    return this.children(child, children, expand).collectList().map(treeNodes -> {
                        treeNode.setChildren(treeNodes);
                        return treeNode;
                    });

                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    return Mono.empty();
                }
            });
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return Flux.empty();
        }
    }

    /**
     * 检查是否上下级节点
     *
     * @param superiorId 上级节点ID
     * @param child      对象实例
     * @return true-是，false-否
     */
    private boolean check(Object superiorId, T child) {
        Class<?> childClass = child.getClass();
        try {
            Object superior = childClass.getMethod("getSuperior").invoke(child);
            return superiorId.equals(superior);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return false;
        }
    }
}
