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

package top.leafage.common.r2dbc;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.relational.core.query.Criteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.List;

/**
 * Reactive service interface for basic CRUD operations.
 *
 * @param <D> DTO type for input data
 * @param <V> VO type for output data
 * @author wq li
 * @since 0.3.4
 */
public interface R2dbcCrudService<D, V> {

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
    default Mono<Page<V>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        return Mono.just(new PageImpl<>(Collections.emptyList()));
    }

    /**
     * Retrieves all records or by given ids.
     *
     * @param ids the given records id .
     * @return a Flux stream of all records.
     */
    default Flux<V> retrieve(List<Long> ids) {
        return Flux.empty();
    }

    /**
     * Fetches a record by its ID.
     *
     * @param id the record ID.
     * @return a Mono containing the record, or an empty Mono if not found.
     */
    default Mono<V> fetch(Long id) {
        return Mono.empty();
    }

    /**
     * Checks if a record exists by it's field.
     *
     * @param field the record's field
     * @param id    the record's id
     * @return a Mono emitting true if the record exists, false otherwise.
     */
    default Mono<Boolean> exists(String field, Long id) {
        return Mono.just(false);
    }

    /**
     * Enable or Disable a record by it's ID.
     *
     * @param id the record ID
     * @return a Mono emitting true if the record enabled/disabled, false otherwise
     */
    default Mono<Boolean> enable(Long id) {
        return Mono.just(false);
    }

    /**
     * Creates a new record.
     *
     * @param d the DTO representing the new record.
     * @return a Mono containing the created record.
     */
    default Mono<V> create(D d) {
        return Mono.empty();
    }

    /**
     * Updates an existing record by its ID.
     *
     * @param id the record ID.
     * @param d  the DTO containing updated data.
     * @return a Mono containing the updated record.
     */
    default Mono<V> modify(Long id, D d) {
        return Mono.empty();
    }

    /**
     * Removes a record by its ID.
     *
     * @param id the record ID.
     * @return a Mono indicating completion.
     */
    default Mono<Void> remove(Long id) {
        return Mono.empty();
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

    private Object convertValue(String field, String value, Class<?> entityClass) {
        if (value == null || value.isBlank()) return null;
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(entityClass, field);
        if (descriptor == null) return null;

        Class<?> targetType = descriptor.getPropertyType();
        return DefaultConversionService.getSharedInstance().convert(value, targetType);
    }
}


