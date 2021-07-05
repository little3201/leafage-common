package top.leafage.common.basic;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface TreeNodeAware<T> {

    /**
     * 处理扩展数据
     *
     * @param treeNode 当前节点
     * @param clazz    数据类型
     * @param t        数据实例
     * @param expand   扩展字段
     */
    default void expand(TreeNode treeNode, Class<?> clazz, T t, Set<String> expand) {
        if (expand != null && !expand.isEmpty()) {
            Map<String, String> map = new HashMap<>(expand.size());
            expand.forEach(filed -> {
                try {
                    String methodSuffix = filed.substring(0, 1).toUpperCase() + filed.substring(1);
                    Object value = clazz.getMethod("get" + methodSuffix).invoke(t);
                    map.put(filed, value != null ? value.toString() : null);
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
            treeNode.setExpand(map);
        }
    }
}
