package top.leafage.common.servlet;

import top.leafage.common.basic.TreeNode;
import top.leafage.common.basic.TreeNodeAware;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ServletTreeNodeAware<T> extends TreeNodeAware<T> {

    /**
     * add child node
     *
     * @param superior superior data
     * @param children child data
     * @return tree node
     */
    default List<TreeNode> children(T superior, List<T> children) {
        return this.children(superior, children, null);
    }

    /**
     * add child node
     *
     * @param superior superior data
     * @param children child data
     * @param expand   expand data
     * @return tree node
     */
    default List<TreeNode> children(T superior, List<T> children, Set<String> expand) {
        Class<?> aClass = superior.getClass();
        try {
            long superiorId = aClass.getDeclaredField("id").getLong(aClass);
            String superiorName = aClass.getDeclaredField("name").get(aClass).toString();

            return children.stream().filter(child -> this.check(superiorId, child))
                    .map(child -> {
                        Class<?> childClass = child.getClass();
                        try {
                            String name = childClass.getDeclaredField("name").get(childClass).toString();
                            String code = childClass.getDeclaredField("code").get(childClass).toString();

                            TreeNode servletTreeNode = new TreeNode(code, name);
                            servletTreeNode.setSuperior(superiorName);
                            servletTreeNode.setChildren(this.children(child, children));

                            // deal expand
                            this.expand(servletTreeNode, childClass, expand);

                            return servletTreeNode;
                        } catch (IllegalAccessException | NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }).collect(Collectors.toList());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
