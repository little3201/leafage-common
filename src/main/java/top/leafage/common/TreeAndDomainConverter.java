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

package top.leafage.common;

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
 * @param <N> the tree node
 * @since 0.3.4
 */
public abstract class TreeAndDomainConverter<N, ID> extends DomainConverter {

    /**
     * Create tree node.
     *
     * @param t      The source object to convert.
     * @param expand The expand data.
     * @return An instance of the tree node.
     * @throws RuntimeException if the conversion fails.
     */
    protected TreeNode<ID> createNode(N t, Set<String> expand) {
        Class<?> clazz = t.getClass();
        ID id = this.getValue(t, clazz, "id");
        if (id == null) throw new IllegalArgumentException("ID must not be null");

        String name = this.getValue(t, clazz, "name");
        ID superiorId = this.getValue(t, clazz, "superiorId");

        Map<String, Object> meta = this.extractMeta(clazz, t, expand);

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
     * @return A list of the tree node.
     * @throws RuntimeException if the conversion fails.
     */
    protected List<TreeNode<ID>> buildTree(List<TreeNode<ID>> nodes) {
        Map<ID, List<TreeNode<ID>>> childrenMap = nodes.stream()
                .filter(n -> n.getSuperiorId() != null)
                .collect(Collectors.groupingBy(TreeNode::getSuperiorId));

        return nodes.stream()
                .peek(node ->
                        node.setChildren(childrenMap.getOrDefault(node.getId(), Collections.emptyList())))
                .filter(node -> node.getSuperiorId() == null)
                .collect(Collectors.toList());
    }

    /**
     * Get value.
     *
     * @param obj          object.
     * @param clazz        object class.
     * @param propertyName property name.
     * @param <V>          type of the value.
     * @return value.
     */
    @SuppressWarnings("unchecked")
    private <V> V getValue(N obj, Class<?> clazz, String propertyName) {
        while (clazz != null) {
            try {
                PropertyDescriptor descriptor = new PropertyDescriptor(propertyName, clazz);
                return (V) descriptor.getReadMethod().invoke(obj);
            } catch (IntrospectionException e) {
                clazz = clazz.getSuperclass(); // 向上找
            } catch (IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Extract meta
     *
     * @param clazz  type.
     * @param obj    object.
     * @param expand data.
     * @return value.
     */
    private Map<String, Object> extractMeta(Class<?> clazz, N obj, Set<String> expand) {
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



