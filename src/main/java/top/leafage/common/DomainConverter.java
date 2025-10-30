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

package top.leafage.common;

import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;

/**
 * This interface includes methods for creating pageable objects and converting entities.
 *
 * @author wq li
 * @since 0.3.4
 */
public abstract class DomainConverter {

    /**
     * must not be null
     */
    public static final String _MUST_NOT_BE_NULL = "The given %s must not be null";

    /**
     * must not be empty
     */
    public static final String _MUST_NOT_BE_EMPTY = "The given %s must not be empty";

    /**
     * id must not be null
     */
    public static final String ID_MUST_NOT_BE_NULL = String.format(_MUST_NOT_BE_NULL, "id");

    /**
     * Creates a {@link org.springframework.data.domain.Pageable} object for pagination and sorting.
     *
     * @param page       The page number (zero-based).
     * @param size       The size of the page (number of items per page), capped at 500.
     * @param sortBy     The field to sort by, or null for unsorted pagination.
     * @param descending Whether the sorting should be in descending order.
     * @return A {@link org.springframework.data.domain.Pageable} instance configured with the provided parameters.
     */
    protected Pageable pageable(int page, int size, String sortBy, boolean descending) {
        if (size > 500) {
            throw new IllegalArgumentException("Page size must be less than 500");
        }

        Sort sort = Sort.by(new Sort.Order(descending ? Sort.Direction.DESC : Sort.Direction.ASC,
                StringUtils.hasText(sortBy) ? sortBy : "id"));

        return PageRequest.of(page, size, sort);
    }

    /**
     * Converts a source object to an existing target object.
     *
     * @param source The source object to convert.
     * @param target The target object to populate.
     * @param <S>    The type of the source object.
     * @param <T>    The type of the target object.
     * @return The populated target object.
     * @throws java.lang.RuntimeException if the conversion fails.
     */
    protected <S, T> T convert(S source, T target) {
        try {
            BeanCopier copier = BeanCopier.create(source.getClass(), target.getClass(), false);
            copier.copy(source, target, null);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Convert error", e);
        }
    }

    /**
     * Converts a source object to an instance of the target class.
     *
     * @param source  The source object to convert.
     * @param voClass The class of the vo object.
     * @param <S>     The type of the source object.
     * @param <T>     The type of the target object.
     * @return An instance of the target class.
     * @throws java.lang.RuntimeException if the conversion fails.
     */
    protected <S, T> T convertToVO(S source, Class<T> voClass) {
        try {
            T target = voClass.getDeclaredConstructor().newInstance();
            return this.convert(source, target);
        } catch (Exception e) {
            throw new RuntimeException("Convert to vo error", e);
        }
    }

    /**
     * Converts a source object to an instance of the target class.
     *
     * @param source      The source object to convert.
     * @param targetClass The class of the target object.
     * @param <S>         The type of the source object.
     * @param <T>         The type of the target object.
     * @return An instance of the target class.
     * @throws java.lang.RuntimeException if the conversion fails.
     */
    protected <S, T> T convertToDomain(S source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            return this.convert(source, target);
        } catch (Exception e) {
            throw new RuntimeException("Convert to domain error", e);
        }
    }

    /**
     * 解析过滤条件字符串并构建查询的Criteria。
     * <p>
     * 过滤条件格式示例： "age:gt:18,status:eq:active,name:like:john"
     * 每个条件由字段名、操作符和对应值组成，三者之间用冒号分隔，
     * 多个条件之间用逗号分隔。
     * <p>
     * 支持的操作符包括：
     * - eq: 等于
     * - ne: 不等于
     * - like: 模糊匹配（SQL LIKE，自动加%前后缀）
     * - gt: 大于
     * - gte: 大于等于
     * - lt: 小于
     * - lte: 小于等于
     *
     * @param filters     过滤条件字符串
     * @param entityClass 实体类型
     * @return Optional封装的Predicate查询条件，若无有效条件则为空.
     * @since 0.3.5
     */
    protected Criteria buildCriteria(String filters, Class<?> entityClass) {
        if (filters == null || filters.trim().isEmpty()) {
            return Criteria.empty();
        }

        String[] parts = filters.split(",");

        Criteria criteria = Criteria.empty();

        for (String part : parts) {
            String[] tokens = part.trim().split(":", 3);
            if (tokens.length != 3) continue;

            String field = tokens[0].trim();
            String op = tokens[1].trim().toLowerCase();
            String value = tokens[2].trim();

            if (!StringUtils.hasText(field) || !StringUtils.hasText(op) || !StringUtils.hasText(value)) {
                continue;
            }

            Criteria condition = switch (op) {
                case "eq" -> Criteria.where(field).is(convertValue(field, value, entityClass));
                case "ne" -> Criteria.where(field).not(convertValue(field, value, entityClass));
                case "like" -> Criteria.where(field).like("%" + value + "%");
                case "gt" -> Criteria.where(field).greaterThan(convertValue(field, value, entityClass));
                case "lt" -> Criteria.where(field).lessThan(convertValue(field, value, entityClass));
                case "gte" -> Criteria.where(field).greaterThanOrEquals(convertValue(field, value, entityClass));
                case "lte" -> Criteria.where(field).lessThanOrEquals(convertValue(field, value, entityClass));
                default -> null;
            };

            criteria = condition == null ? criteria : criteria.and(condition);
        }
        return criteria;
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
    private Object convertValue(String field, String value, Class<?> entityClass) {
        if (value == null || value.isBlank()) return null;
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(entityClass, field);
        if (descriptor == null) return null;

        Class<?> targetType = descriptor.getPropertyType();
        return DefaultConversionService.getSharedInstance().convert(value, targetType);
    }

}
