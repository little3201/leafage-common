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

package top.leafage.common.data.reactive;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.data.Service;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.List;

/**
 * Reactive service interface.
 *
 * @param <D> The dto type.
 * @param <V> the vo type.
 * @author wq li
 * @since 0.3.7
 */
public interface ReactiveCrudService<D, V> extends Service {

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
     * Retrieves all records or by given pks.
     *
     * @param ids the given records id .
     * @return a Flux stream of all records.
     */
    default Flux<V> retrieve(List<Long> ids) {
        return Flux.empty();
    }

    /**
     * Fetch a record by pk.
     *
     * @param id the pk.
     * @return a Mono containing the record, or an empty Mono if not found.
     */
    default Mono<V> fetch(Long id) {
        return Mono.empty();
    }

    /**
     * Enable or Disable a record by pk.
     *
     * @param id the pk.
     * @return a Mono emitting true if the record enabled/disabled, false otherwise
     */
    default Mono<Boolean> enable(Long id) {
        return Mono.just(false);
    }

    /**
     * Creates a new record.
     *
     * @param dto the dto.
     * @return a Mono containing the created record.
     */
    default Mono<V> create(D dto) {
        return Mono.empty();
    }

    /**
     * Creates all given records.
     *
     * @param iterable the dto iterable.
     * @return the created records.
     */
    default Flux<V> createAll(Iterable<D> iterable) {
        return Flux.empty();
    }

    /**
     * Updates an existing record by pk.
     *
     * @param id  the pk.
     * @param dto the dto.
     * @return a Mono containing the updated record.
     */
    default Mono<V> modify(Long id, D dto) {
        return Mono.empty();
    }

    /**
     * Removes a record by pk.
     *
     * @param id the pk.
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
     * @return Optional封装的Predicate查询条件，若无有效条件则为空.
     * @since 0.3.5
     */
    default Criteria buildCriteria(String filters, Class<?> entityClass) {
        if (!StringUtils.hasText(filters)) {
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

            Object convertedValue = convertValue(field, value, entityClass);
            if (convertedValue == null) {
                return criteria.and(Criteria.where(field).isNull());
            }
            Criteria condition = switch (op) {
                case "eq" -> Criteria.where(field).is(convertedValue);
                case "ne" -> Criteria.where(field).not(convertedValue);
                case "like" -> Criteria.where(field).like("%" + value + "%");
                case "gt" -> Criteria.where(field).greaterThan(convertedValue);
                case "lt" -> Criteria.where(field).lessThan(convertedValue);
                case "gte" -> Criteria.where(field).greaterThanOrEquals(convertedValue);
                case "lte" -> Criteria.where(field).lessThanOrEquals(convertedValue);
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
        if (!StringUtils.hasText(value)) return null;
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(entityClass, field);
        if (descriptor == null) return null;

        Class<?> targetType = descriptor.getPropertyType();
        return DefaultConversionService.getSharedInstance().convert(value, targetType);
    }
}


