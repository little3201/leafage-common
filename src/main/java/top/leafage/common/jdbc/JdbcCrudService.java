/*
 *  Copyright 2018-2025 little3201.
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

package top.leafage.common.jdbc;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Servlet service interface for jdbc CRUD operations.
 *
 * @param <D> DTO type for input data
 * @param <V> VO type for output data
 * @since 0.3.4
 * @author wq li
 */
public interface JdbcCrudService<D, V> {

    /**
     * Retrieves records by pageable, sort, filters.
     *
     * @param page       The page number (zero-based).
     * @param size       The size of the page (number of items per page), capped at 500.
     * @param sortBy     The field to sort by, or null for unsorted pagination.
     * @param descending Whether the sorting should be in descending order.
     * @param filters    filters to apply to the query.
     * @return a Flux stream of all records.
     */
    default Page<V> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        return new PageImpl<>(Collections.emptyList());
    }

    /**
     * Retrieves all records or by given ids.
     *
     * @param ids the given records id .
     * @return a list of all records
     */
    default List<V> retrieve(List<Long> ids) {
        return Collections.emptyList();
    }

    /**
     * Fetches a record by its ID.
     *
     * @param id the record ID
     * @return the record, or null if not found
     */
    default V fetch(Long id) {
        return null;
    }

    /**
     * Checks if a record exists by it's field.
     *
     * @param field the record's field
     * @param id    the record's id
     * @return true if the record exists, false otherwise
     */
    default boolean exists(String field, Long id) {
        return false;
    }

    /**
     * Enable or Disable a record by it's ID.
     *
     * @param id the record ID
     * @return true if the record enabled/disabled, false otherwise
     */
    default boolean enable(Long id) {
        return false;
    }

    /**
     * Creates a new record.
     *
     * @param d the new record
     * @return the created record
     */
    default V create(D d) {
        return null;
    }

    /**
     * Creates all given records.
     *
     * @param iterable the new records.
     * @return the created records.
     */
    default List<V> createAll(Iterable<D> iterable) {
        return Collections.emptyList();
    }

    /**
     * Updates an existing record by its ID.
     *
     * @param id the record ID
     * @param d  the DTO containing updated data
     * @return the updated record
     */
    default V modify(Long id, D d) {
        return null;
    }

    /**
     * Removes a record by its ID.
     *
     * @param id the record ID
     */
    default void remove(Long id) {
    }

    /**
     * 解析过滤条件字符串并构建查询的 Example。
     * <p>
     * 过滤条件格式示例： "status:eq:active,name:like:john"
     * 每个条件由字段名、操作符和对应值组成，三者之间用冒号分隔，
     * 多个条件之间用逗号分隔。
     * <p>
     * 支持的操作符包括：
     * - eq: 等于
     * - like: 模糊匹配（SQL LIKE，自动加%前后缀）
     *
     * @param filters     过滤条件字符串
     * @param entityClass 对象类型
     * @param <T>         实体类型泛型
     * @return Example查询条件，若无有效条件则为空
     */
    default <T> Example<T> buildExample(String filters, Class<T> entityClass) {
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 构造空实例以便填充
        T instance;
        try {
            instance = entityClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Constructor error", e);
        }

        if (filters != null && !filters.trim().isEmpty()) {
            String[] parts = filters.split(",");

            for (String part : parts) {
                String[] tokens = part.trim().split(":", 3);
                if (tokens.length != 3) continue;

                String field = tokens[0].trim();
                String op = tokens[1].toLowerCase().trim();
                String value = tokens[2].trim();

                // 根据操作符设置匹配条件
                switch (op) {
                    case "eq":
                        matcher = matcher.withMatcher(field, ExampleMatcher.GenericPropertyMatchers.exact());
                        setFieldValue(instance, field, value);
                        break;
                    case "like":
                        matcher = matcher.withMatcher(field, ExampleMatcher.GenericPropertyMatchers.contains());
                        setFieldValue(instance, field, value);
                        break;
                    default:
                }
            }
        }

        return Example.of(instance, matcher);
    }

    /**
     * <p>setFieldValue.</p>
     *
     * @param instance a T object
     * @param field a {@link java.lang.String} object
     * @param value a {@link java.lang.String} object
     * @param <T> a T class
     */
    private <T> void setFieldValue(T instance, String field, String value) {
        try {
            PropertyDescriptor descriptor = getPropertyDescriptor(instance.getClass(), field);
            // 获取对应的 setter 方法
            Method setter = descriptor.getWriteMethod();
            if (setter != null) {
                // 根据 setter 的参数类型进行转换
                Class<?> parameterType = descriptor.getPropertyType();
                Object convertedValue = DefaultConversionService.getSharedInstance().convert(value, parameterType);
                setter.invoke(instance, convertedValue);
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to set field value", e);
        }
    }

    /**
     * <p>getPropertyDescriptor.</p>
     *
     * @param clazz a {@link java.lang.Class} object
     * @param propertyName a {@link java.lang.String} object
     * @return a {@link java.beans.PropertyDescriptor} object
     * @throws java.beans.IntrospectionException if any.
     */
    private PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) throws IntrospectionException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return new PropertyDescriptor(propertyName, current);
            } catch (IntrospectionException e) {
                current = current.getSuperclass();
            }
        }
        throw new IntrospectionException("Property '" + propertyName + "' not found in class hierarchy of " + clazz.getName());
    }
}

