/*
 *  Copyright 2018-2023 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package top.leafage.common;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Construct tree
 *
 * @author liwenqiang 2021-07-21 20:08
 * @since 0.1.3
 */
public abstract class AbstractTreeNodeService<T> extends AbstractBasicService {

    private static final String NAME = "name";
    private static final String CODE = "code";
    private static final String SUPERIOR = "superior";

    private static final Logger log = StatusLogger.getLogger();

    /**
     * 构造 TreeNode 对象
     *
     * @param t      实例数据
     * @param expand 扩展字段
     * @return TreeNode 对象
     * @since 0.1.7
     */
    protected TreeNode node(T t, Set<String> expand) {
        Class<?> childClass = t.getClass();
        Object code = this.getCode(t, childClass);
        Object name = this.getName(t, childClass);
        Object superior = this.getSuperior(t, childClass);

        TreeNode treeNode = new TreeNode(Objects.nonNull(code) ? String.valueOf(code) : null,
                Objects.nonNull(name) ? String.valueOf(name) : null);
        treeNode.setSuperior(Objects.nonNull(superior) ? String.valueOf(superior) : null);

        // deal expand
        this.expand(treeNode, childClass, t, expand);
        return treeNode;
    }

    protected List<TreeNode> nodes(List<TreeNode> treeNodes) {
        Map<String, List<TreeNode>> listMap = treeNodes.stream().filter(node -> Objects.nonNull(node.getSuperior()) &&
                        !"0".equals(node.getSuperior()))
                .collect(Collectors.groupingBy(TreeNode::getSuperior));
        // get children from grouped map
        treeNodes.forEach(node -> node.setChildren(listMap.get(node.getCode())));
        return treeNodes.stream().filter(node -> Objects.isNull(node.getSuperior()) || "0".equals(node.getSuperior()))
                .collect(Collectors.toList());
    }

    /**
     * 扩展数据
     *
     * @param treeNode 当前节点
     * @param clazz    数据类型
     * @param t        数据实例
     * @param expand   扩展字段
     */
    private void expand(TreeNode treeNode, Class<?> clazz, T t, Set<String> expand) {
        if (expand != null && !expand.isEmpty()) {
            Map<String, Object> map = new HashMap<>(expand.size());
            expand.forEach(filed -> {
                try {
                    PropertyDescriptor superIdDescriptor = new PropertyDescriptor(filed, clazz);
                    Object value = superIdDescriptor.getReadMethod().invoke(t);

                    map.put(filed, value);
                } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
                    log.error("expand data error.", e);
                }
            });
            treeNode.setExpand(map);
        }
    }

    /**
     * 获取name
     *
     * @param t     对象
     * @param clazz 类型
     * @return name
     */
    protected Object getName(T t, Class<?> clazz) {
        Object name = null;
        try {
            PropertyDescriptor superIdDescriptor = new PropertyDescriptor(NAME, clazz);
            name = superIdDescriptor.getReadMethod().invoke(t);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("get name error.", e);
        }
        return name;
    }

    /**
     * 获取code
     *
     * @param t     对象
     * @param clazz 类型
     * @return code
     */
    protected Object getCode(T t, Class<?> clazz) {
        Object code = null;
        try {
            PropertyDescriptor superIdDescriptor = new PropertyDescriptor(CODE, clazz);
            code = superIdDescriptor.getReadMethod().invoke(t);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("get code error.", e);
        }
        return code;
    }

    /**
     * 获取superior
     *
     * @param t     对象
     * @param clazz 类型
     * @return superior
     */
    private Object getSuperior(T t, Class<?> clazz) {
        Object superior = null;
        try {
            PropertyDescriptor superDescriptor = new PropertyDescriptor(SUPERIOR, clazz);
            superior = superDescriptor.getReadMethod().invoke(t);
            // superior code
            if (Objects.nonNull(superior)) {
                Class<?> aClass = superior.getClass();
                PropertyDescriptor superCodeDescriptor = new PropertyDescriptor(CODE, aClass);
                superior = superCodeDescriptor.getReadMethod().invoke(t);
            }

        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("get superior error.", e);
        }
        return superior;
    }
}
