/*
 * Copyright (c) 2025.  little3201.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.leafage.common.data.converter;

import org.springframework.beans.BeanUtils;
import top.leafage.common.data.domain.TreeNode;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Methods for constructing tree structures from objects.
 *
 * @author wq li
 * @since 0.3.4
 */
public abstract class AbstractTreeNodeConverter {

    /**
     * Create tree node.
     *
     * @param t      The source object to convert.
     * @param expand The expand data.
     * @param <T>    the source type
     * @param <ID>   the pk type
     * @return An instance of the tree node.
     * @throws java.lang.RuntimeException if the conversion fails.
     */
    public static <T, ID> TreeNode<ID> createNode(T t, Set<String> expand) {
        Class<?> clazz = t.getClass();
        ID id = getValue(t, clazz, "id");
        if (id == null) throw new IllegalArgumentException("ID must not be null");

        String name = getValue(t, clazz, "name");
        ID superiorId = getValue(t, clazz, "superiorId");

        Map<String, Object> meta = extractMeta(clazz, t, expand);

        return TreeNode.withId(id)
                .name(name)
                .superiorId(superiorId)
                .meta(meta)
                .build();
    }

    /**
     * Build tree node.
     *
     * @param nodes The tree nodes.
     * @param <ID>  the pk type
     * @return A list of the tree node.
     * @throws java.lang.RuntimeException if the conversion fails.
     */
    public static <ID> List<TreeNode<ID>> buildTree(List<TreeNode<ID>> nodes) {
        Map<ID, List<TreeNode<ID>>> childrenMap = nodes.stream()
                .filter(n -> n.getSuperiorId() != null)
                .collect(Collectors.groupingBy(TreeNode::getSuperiorId));

        return nodes.stream()
                .peek(node ->
                        node.setChildren(childrenMap.getOrDefault(node.getId(), Collections.emptyList())))
                .filter(node -> node.getSuperiorId() == null)
                .toList();
    }

    /**
     * Get value, if not found, find from it's super class.
     *
     * @param obj          object.
     * @param clazz        object class.
     * @param propertyName property name.
     * @param <T>          the source type
     * @param <V>          the value type.
     * @return value.
     */
    @SuppressWarnings("unchecked")
    private static <T, V> V getValue(T obj, Class<?> clazz, String propertyName) {
        try {
            PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(clazz, propertyName);
            if (descriptor == null) {
                BeanUtils.getPropertyDescriptor(clazz.getSuperclass(), propertyName);
            }
            if (descriptor != null) {
                return (V) descriptor.getReadMethod().invoke(obj);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to get field value: " + propertyName, e);
        }
        return null;
    }

    /**
     * Extract meta
     *
     * @param clazz  type.
     * @param obj    object.
     * @param expand data.
     * @param <T>    the source type
     * @return value.
     */
    private static <T> Map<String, Object> extractMeta(Class<?> clazz, T obj, Set<String> expand) {
        Map<String, Object> meta = new HashMap<>();
        if (expand != null) {
            for (String field : expand) {
                Object value = getValue(obj, clazz, field);
                if (value != null) {
                    meta.put(field, value);
                }
            }
        }
        return meta;
    }
}



