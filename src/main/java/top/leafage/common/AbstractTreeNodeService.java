/*
 *  Copyright 2018-2024 little3201.
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
 * Constructs a tree node service.
 *
 * @author liwenqiang 2021-07-21 20:08
 * @since 0.1.3
 */
public abstract class AbstractTreeNodeService<T> {

    private static final String ID = "id";
    // field suffix, like groupName, roleName suffix is Name
    private static final String NAME = "name";
    private static final String SUPERIOR_ID = "superiorId";

    private static final Logger log = StatusLogger.getLogger();

    /**
     * Constructs a tree node.
     *
     * @param t      Object
     * @param expand Set of properties to expand
     * @return TreeNode object
     * @since 0.2.0
     */
    protected TreeNode node(T t, Set<String> expand) {
        Class<?> aClass = t.getClass();
        // id from superior class
        Object id = this.getId(t, aClass.getSuperclass().getSuperclass());
        // name form class
        Object name = this.getName(t, aClass);
        // superiorId from class
        Object superiorId = this.getSuperiorId(t, aClass);

        TreeNode treeNode = new TreeNode(Objects.nonNull(id) ? (Long) id : null,
                Objects.nonNull(name) ? String.valueOf(name) : null);
        // set superior
        treeNode.setSuperior(Objects.nonNull(superiorId) ? (Long) superiorId : null);

        // deal expand
        this.expand(treeNode, aClass, t, expand);
        return treeNode;
    }

    /**
     * Gets and sets children.
     *
     * @param treeNodes List of children
     * @return List of TreeNode objects
     * @since 0.2.0
     */
    protected List<TreeNode> children(List<TreeNode> treeNodes) {
        Map<Long, List<TreeNode>> listMap = treeNodes.stream().filter(node -> Objects.nonNull(node.getSuperior()) &&
                        0 == node.getSuperior())
                .collect(Collectors.groupingBy(TreeNode::getSuperior));
        // get children from grouped map
        treeNodes.forEach(node -> node.setChildren(listMap.get(node.getId())));
        return treeNodes.stream().filter(node -> Objects.isNull(node.getSuperior()) || 0 == node.getSuperior())
                .collect(Collectors.toList());
    }

    /**
     * Expand data.
     *
     * @param treeNode TreeNode object
     * @param clazz    Class type
     * @param t        Object
     * @param expand   Set of properties to expand
     */
    private void expand(TreeNode treeNode, Class<?> clazz, T t, Set<String> expand) {
        if (expand != null && !expand.isEmpty()) {
            Map<String, Object> map = new HashMap<>(expand.size());

            try {
                PropertyDescriptor descriptor;
                for (String field : expand) {
                    descriptor = new PropertyDescriptor(field, clazz);
                    Object value = descriptor.getReadMethod().invoke(t);
                    map.put(field, value);
                }
            } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
                log.error("Expand data error.", e);
            }
            treeNode.setExpand(map);
        }
    }

    /**
     * Gets the ID.
     *
     * @param obj   Object instance
     * @param clazz Class type
     * @return ID value
     */
    protected Object getId(Object obj, Class<?> clazz) {
        Object id = null;
        try {
            PropertyDescriptor idDescriptor = new PropertyDescriptor(ID, clazz);
            id = idDescriptor.getReadMethod().invoke(obj);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("Get id error.", e);
        }
        return id;
    }

    /**
     * Gets the name.
     *
     * @param t     Object
     * @param clazz Class type
     * @return Name value
     */
    protected Object getName(T t, Class<?> clazz) {
        Object name = null;
        try {
            // name
            PropertyDescriptor nameDescriptor = new PropertyDescriptor(NAME, clazz);
            name = nameDescriptor.getReadMethod().invoke(t);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("Get name error.", e);
        }
        return name;
    }

    /**
     * Gets the superior ID.
     *
     * @param t     Object
     * @param clazz Class type
     * @return Superior ID value
     */
    private Object getSuperiorId(T t, Class<?> clazz) {
        Object superiorId = null;
        try {
            PropertyDescriptor superiorIdDescriptor = new PropertyDescriptor(SUPERIOR_ID, clazz);
            superiorId = superiorIdDescriptor.getReadMethod().invoke(t);

        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("Get superiorId error.", e);
        }
        return superiorId;
    }
}
