package top.leafage.common.basic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface TreeNodeAware<T> {

    default boolean check(long superiorId, T child) {
        Class<?> childClass = child.getClass();
        try {
            long id = childClass.getDeclaredField("id").getLong(childClass);
            return superiorId == id;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return false;
        }
    }

    default void expand(TreeNode treeNode, Class<?> clazz, Set<String> expand) {
        if (expand != null && !expand.isEmpty()) {
            Map<String, String> map = new HashMap<>(expand.size());
            expand.forEach(filed -> {
                try {
                    String value = clazz.getDeclaredField(filed).get(clazz).toString();
                    map.put(filed, value);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            });
            treeNode.setExpand(map);
        }
    }
}
