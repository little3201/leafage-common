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

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.relational.core.query.Criteria;
import top.leafage.common.CrudService;

import java.beans.PropertyDescriptor;

/**
 * Servlet service interface for jdbc CRUD operations.
 *
 * @param <D> DTO type for input data
 * @param <V> VO type for output data
 * @author wq li
 * @since 0.3.4
 */
public interface JdbcCrudService<D, V> extends CrudService<D, V> {

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
     * @return Optional封装的Predicate查询条件，若无有效条件则为空
     */
    default Criteria buildCriteria(String filters, Class<?> entityClass) {
        if (filters == null || filters.trim().isEmpty()) {
            return Criteria.empty();
        }

        String[] parts = filters.split(",");

        Criteria criteria = Criteria.empty();

        for (String part : parts) {
            String[] tokens = part.split(":", 3);
            if (tokens.length != 3) continue;

            String field = tokens[0].trim();
            String op = tokens[1].trim().toLowerCase();
            String value = tokens[2].trim();

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
     */
    private Object convertValue(String field, String value, Class<?> entityClass) {
        if (value == null || value.isBlank()) return null;
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(entityClass, field);
        if (descriptor == null) return null;

        Class<?> targetType = descriptor.getPropertyType();
        return DefaultConversionService.getSharedInstance().convert(value, targetType);
    }
}

