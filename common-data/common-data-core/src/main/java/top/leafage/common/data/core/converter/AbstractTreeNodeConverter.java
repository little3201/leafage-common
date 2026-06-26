/*
 * Copyright (c) 2025-2026.  little3201.
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

package top.leafage.common.data.core.converter;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.StringUtils;
import top.leafage.common.data.core.domain.TreeNode;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Methods for constructing tree structures from objects.
 *
 * @author wq li
 */
public abstract class AbstractTreeNodeConverter {

    private static final Map<Class<?>, Map<String, PropertyDescriptor>> PPOPERTY_CACHE = new ConcurrentHashMap<>();

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
        if (id == null) throw new IllegalArgumentException("The given id must not be null");

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
    public static <T, V> V getValue(T obj, Class<?> clazz, String propertyName) {
        if (obj == null || clazz == null || propertyName == null || propertyName.isEmpty()) {
            return null;
        }

        try {
            PropertyDescriptor descriptor = getDescriptor(clazz, propertyName);

            if (descriptor != null) {
                Method readMethod = descriptor.getReadMethod();
                if (readMethod != null) {
                    return (V) readMethod.invoke(obj);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to get property: " + propertyName + " from " + clazz.getName(), e
            );
        }

        return null;
    }

    /**
     * convert value.
     *
     * @param field       a {@link java.lang.String} object
     * @param value       a {@link java.lang.String} object
     * @param entityClass entity class
     * @return Object value.
     * @since 0.3.5
     */
    public static Object convertValue(String field, String value, Class<?> entityClass) {
        if (!StringUtils.hasText(value)) return null;
        PropertyDescriptor descriptor = getDescriptor(entityClass, field);
        if (descriptor == null) return null;

        Class<?> targetType = descriptor.getPropertyType();
        return DefaultConversionService.getSharedInstance().convert(value, targetType);
    }

    private static PropertyDescriptor getDescriptor(Class<?> clazz, String propertyName) {
        return PPOPERTY_CACHE.computeIfAbsent(clazz, AbstractTreeNodeConverter::scanClass)
                .get(propertyName);
    }

    private static Map<String, PropertyDescriptor> scanClass(Class<?> clazz) {
        Map<String, PropertyDescriptor> map = new ConcurrentHashMap<>();

        Class<?> current = clazz;

        // 只查当前类 + 直接父类
        for (int i = 0; i < 2 && current != null && current != Object.class; i++) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(current, Object.class);
                PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

                for (PropertyDescriptor pd : pds) {
                    map.putIfAbsent(pd.getName(), pd);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to scan class: " + clazz.getName(), e);
            }

            current = current.getSuperclass();
        }

        return map;
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



