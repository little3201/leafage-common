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
 * Abstract base service for constructing tree structures from objects.
 * Provides functionality for creating tree nodes and organizing them
 * based on superior-subordinate relationships.
 *
 * @param <T> the type of object representing a node in the tree
 * @since 0.1.3
 */
public abstract class AbstractTreeNodeService<T> {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String SUPERIOR_ID = "superiorId";

    private static final Logger log = StatusLogger.getLogger();

    /**
     * Creates a tree node from the given object, using the provided property names to expand additional data.
     * This method extracts ID, name, and superior ID from the object and attaches any expanded properties.
     *
     * @param t      the object representing a node.
     * @param expand a set of property names to be expanded into the node's additional properties.
     * @return a fully constructed TreeNode instance.
     * @throws IllegalArgumentException if the ID property is null.
     * @since 0.3.0
     */
    protected TreeNode createNode(T t, Set<String> expand) {
        Class<?> aClass = t.getClass();
        Object id = this.getId(t, aClass.getSuperclass());
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Object name = this.getName(t, aClass);
        Object superiorId = this.getSuperiorId(t, aClass);

        return TreeNode.withId((Long) id)
                .name(Objects.nonNull(name) ? String.valueOf(name) : null)
                .superiorId(Objects.nonNull(superiorId) ? (Long) superiorId : null)
                .meta(meta(aClass, t, expand)).build();
    }

    /**
     * Organizes the tree nodes by assigning children to their respective parents based on the superior ID.
     * Returns a list of root nodes (nodes that do not have a superior).
     *
     * @param treeNodes the list of all nodes to organize.
     * @return a list of root nodes, each containing its child nodes.
     * @since 0.2.0
     */
    protected List<TreeNode> children(List<TreeNode> treeNodes) {
        Map<Long, List<TreeNode>> nodesMap = treeNodes.stream()
                .filter(node -> Objects.nonNull(node.getSuperiorId()) && node.getSuperiorId() != 0)
                .collect(Collectors.groupingBy(TreeNode::getSuperiorId));

        return treeNodes.stream()
                .map(treeNode -> TreeNode.withId(treeNode.getId())
                        .name(treeNode.getName())
                        .superiorId(treeNode.getSuperiorId())
                        .children(nodesMap.get(treeNode.getId()))
                        .meta(treeNode.getMeta())
                        .build())
                .filter(node -> Objects.isNull(node.getSuperiorId()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the ID value from the given object using reflection.
     *
     * @param obj   the object instance.
     * @param clazz the class of the object or its superclass.
     * @return the ID value, or null if an error occurs during reflection.
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
     * Retrieves the name value from the given object using reflection.
     *
     * @param t     the object instance.
     * @param clazz the class of the object.
     * @return the name value, or null if an error occurs during reflection.
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
     * Retrieves the superior ID value from the given object using reflection.
     *
     * @param t     the object instance.
     * @param clazz the class of the object.
     * @return the superior ID value, or null if an error occurs during reflection.
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
     * Expands additional properties for the TreeNode by reflecting on the object's class and property names.
     *
     * @param clazz  the class of the object.
     * @param t      the object representing the node.
     * @param expand a set of property names to expand as additional data.
     * @return a map containing the expanded properties.
     * @since 0.3.0
     */
    private Map<String, Object> meta(Class<?> clazz, T t, Set<String> expand) {
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


