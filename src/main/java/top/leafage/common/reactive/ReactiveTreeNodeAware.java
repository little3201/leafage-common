package top.leafage.common.reactive;

import reactor.core.publisher.Flux;
import top.leafage.common.basic.TreeNode;
import top.leafage.common.basic.TreeNodeAware;
import java.util.Set;

public interface ReactiveTreeNodeAware<T> extends TreeNodeAware<T> {

    /**
     * add child node
     *
     * @param superior superior data
     * @param children child data
     * @return tree node
     */
    default Flux<TreeNode> children(T superior, Flux<T> children) {
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
    default Flux<TreeNode> children(T superior, Flux<T> children, Set<String> expand) {
        Class<?> aClass = superior.getClass();
        try {
            long superiorId = aClass.getDeclaredField("id").getLong(aClass);
            String superiorName = aClass.getDeclaredField("name").get(aClass).toString();

            return children.filter(child -> this.check(superiorId, child))
                    .map(child -> {
                        Class<?> childClass = child.getClass();
                        try {
                            String name = childClass.getDeclaredField("name").get(childClass).toString();
                            String code = childClass.getDeclaredField("code").get(childClass).toString();

                            TreeNode treeNode = new TreeNode(code, name);
                            treeNode.setSuperior(superiorName);
                            this.children(child, children, expand).collectList().map(treeNodes -> {
                                treeNode.setChildren(treeNodes);
                                return treeNode;
                            });

                            // deal expand
                            this.expand(treeNode, childClass, expand);

                            return treeNode;
                        } catch (IllegalAccessException | NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Flux.empty();
    }

}
