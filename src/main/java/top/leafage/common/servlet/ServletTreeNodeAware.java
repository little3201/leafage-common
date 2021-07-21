package top.leafage.common.servlet;

import top.leafage.common.basic.TreeNode;
import top.leafage.common.basic.TreeNodeAware;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ServletTreeNodeAware<T> extends TreeNodeAware<T> {

    /**
     * 处理子节点
     *
     * @param superior 上级数据
     * @param children 子节点
     * @return 树节点数据集
     */
    default List<TreeNode> children(T superior, List<T> children) {
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
    default List<TreeNode> children(T superior, List<T> children, Set<String> expand) {
        Class<?> aClass = superior.getClass();
        try {
            Long superiorId = (Long) aClass.getSuperclass().getMethod("getId").invoke(superior);
            Object superiorName = aClass.getMethod("getName").invoke(superior);

            return children.stream().filter(child -> this.check(superiorId, child)).map(child -> {
                Class<?> childClass = child.getClass();
                try {
                    Object name = childClass.getMethod("getName").invoke(child);
                    Object code = childClass.getMethod("getCode").invoke(child);

                    TreeNode treeNode = new TreeNode(code == null ? null : code.toString(),
                            name == null ? null : name.toString());
                    treeNode.setSuperior(superiorName == null ? null : superiorName.toString());

                    // deal expand
                    this.expand(treeNode, childClass, child, expand);

                    treeNode.setChildren(this.children(child, children, expand));

                    return treeNode;
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 检查是否上下级节点
     *
     * @param superiorId 上级节点ID
     * @param child      对象实例
     * @return true-是，false-否
     */
    default boolean check(Long superiorId, T child) {
        Class<?> childClass = child.getClass();
        try {
            Long superior = (Long) childClass.getMethod("getSuperior").invoke(child);
            return superiorId.equals(superior);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return false;
        }
    }
}
