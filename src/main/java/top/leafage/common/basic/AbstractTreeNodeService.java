package top.leafage.common.basic;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractTreeNodeService<T> extends AbstractBasicService {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String CODE = "code";
    private static final String SUPERIOR = "superior";

    /**
     * 处理扩展数据
     *
     * @param treeNode 当前节点
     * @param clazz    数据类型
     * @param t        数据实例
     * @param expand   扩展字段
     */
    protected void expand(TreeNode treeNode, Class<?> clazz, T t, Set<String> expand) {
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

    /**
     * 获取ID
     *
     * @param t      对象
     * @param aClass 类型
     * @return ID
     */
    protected Object getId(T t, Class<?> aClass) {
        Object superiorId = null;
        try {
            // ID是集成基础父类的，所以要通过superClass获取
            PropertyDescriptor superIdDescriptor = new PropertyDescriptor(ID, aClass.getSuperclass());
            superiorId = superIdDescriptor.getReadMethod().invoke(t);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return superiorId;
    }

    /**
     * 获取name
     *
     * @param t      对象
     * @param aClass 类型
     * @return name
     */
    protected Object getName(T t, Class<?> aClass) {
        Object name = null;
        try {
            PropertyDescriptor superIdDescriptor = new PropertyDescriptor(NAME, aClass);
            name = superIdDescriptor.getReadMethod().invoke(t);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 获取code
     *
     * @param t      对象
     * @param aClass 类型
     * @return code
     */
    protected Object getCode(T t, Class<?> aClass) {
        Object code = null;
        try {
            PropertyDescriptor superIdDescriptor = new PropertyDescriptor(CODE, aClass);
            code = superIdDescriptor.getReadMethod().invoke(t);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 获取superior
     *
     * @param t      对象
     * @param aClass 类型
     * @return superior
     */
    protected Object getSuperior(T t, Class<?> aClass) {
        Object superior = null;
        try {
            PropertyDescriptor superIdDescriptor = new PropertyDescriptor(SUPERIOR, aClass);
            superior = superIdDescriptor.getReadMethod().invoke(t);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return superior;
    }

    /**
     * 检查是否上下级节点
     *
     * @param superiorId 上级节点ID
     * @param child      对象实例
     * @return true-是，false-否
     */
    protected boolean check(Object superiorId, T child) {
        Object superior = this.getSuperior(child, child.getClass());
        return superiorId.equals(superior);
    }
}
