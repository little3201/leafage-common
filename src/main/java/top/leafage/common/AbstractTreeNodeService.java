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
 * Abstract service for constructing tree nodes from objects.
 *
 * @param <T> the type of object representing a node
 * @author wq li
 * @since 0.1.3
 */
public abstract class AbstractTreeNodeService<T> {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String SUPERIOR_ID = "superiorId";

    private static final Logger log = StatusLogger.getLogger();

    /**
     * Constructs a tree node from an object, optionally expanding additional properties.
     *
     * @param t      the object representing the node
     * @param expand a set of property names to expand on the node
     * @return a constructed TreeNode
     * @since 0.2.0
     */
    protected TreeNode node(T t, Set<String> expand) {
        Class<?> aClass = t.getClass();
        Object id = this.getId(t, aClass.getSuperclass());
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Object name = this.getName(t, aClass);
        Object superiorId = this.getSuperiorId(t, aClass);

        return TreeNode.withId((Long) id)
                .name(Objects.nonNull(name) ? String.valueOf(name) : null)
                .superiorId(Objects.nonNull(superiorId) ? (Long) superiorId : null)
                .expand(this.expand(aClass, t, expand)).build();
    }

    /**
     * Sets the children for tree nodes based on their superior IDs.
     *
     * @param treeNodes the list of tree nodes
     * @return a list of root nodes (nodes without a superior)
     * @since 0.2.0
     */
    protected List<TreeNode> children(List<TreeNode> treeNodes) {
        Map<Long, List<TreeNode>> nodesMap = treeNodes.stream()
                .filter(node -> Objects.nonNull(node.getSuperiorId()) && node.getSuperiorId() != 0)
                .collect(Collectors.groupingBy(TreeNode::getSuperiorId));

        return treeNodes.stream().map(treeNode -> TreeNode.withId(treeNode.getId())
                        .name(treeNode.getName())
                        .superiorId(treeNode.getSuperiorId())
                        .children(nodesMap.get(treeNode.getId()))
                        .expand(treeNode.getExpand())
                        .build())
                .filter(node -> Objects.isNull(node.getSuperiorId()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the ID from the object.
     *
     * @param obj   the object instance
     * @param clazz the class of the object
     * @return the ID value, or null if an error occurs
     */
    private Object getId(Object obj, Class<?> clazz) {
        try {
            PropertyDescriptor idDescriptor = new PropertyDescriptor(ID, clazz);
            return idDescriptor.getReadMethod().invoke(obj);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error retrieving ID.", e);
            return null;
        }
    }

    /**
     * Retrieves the name from the object.
     *
     * @param t     the object instance
     * @param clazz the class of the object
     * @return the name value, or null if an error occurs
     */
    private Object getName(T t, Class<?> clazz) {
        try {
            PropertyDescriptor nameDescriptor = new PropertyDescriptor(NAME, clazz);
            return nameDescriptor.getReadMethod().invoke(t);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error retrieving name.", e);
            return null;
        }
    }

    /**
     * Retrieves the superior ID from the object.
     *
     * @param t     the object instance
     * @param clazz the class of the object
     * @return the superior ID value, or null if an error occurs
     */
    private Object getSuperiorId(T t, Class<?> clazz) {
        try {
            PropertyDescriptor superiorIdDescriptor = new PropertyDescriptor(SUPERIOR_ID, clazz);
            return superiorIdDescriptor.getReadMethod().invoke(t);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error retrieving superior ID.", e);
            return null;
        }
    }

    /**
     * Expands additional properties for a TreeNode.
     *
     * @param clazz  the class of the object
     * @param t      the object representing the node
     * @param expand a set of property names to expand
     */
    private Map<String, Object> expand(Class<?> clazz, T t, Set<String> expand) {
        Map<String, Object> expandedData = Collections.emptyMap();
        if (expand != null && !expand.isEmpty()) {
            expandedData = new HashMap<>(expand.size());
            try {
                for (String field : expand) {
                    PropertyDescriptor descriptor = new PropertyDescriptor(field, clazz);
                    Object value = descriptor.getReadMethod().invoke(t);
                    expandedData.put(field, value);
                }
            } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
                log.error("Error expanding data for TreeNode.", e);
            }
        }
        return expandedData;
    }

}

