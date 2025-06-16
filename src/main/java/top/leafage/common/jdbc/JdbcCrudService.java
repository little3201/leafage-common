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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static top.leafage.common.DomainConverter.convertType;

/**
 * Servlet service interface for basic CRUD operations.
 *
 * @param <D> DTO type for input data
 * @param <V> VO type for output data
 * @since 0.3.4
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
     * 解析过滤条件字符串并构建查询的Predicate。
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
     * @param filters 过滤条件字符串
     * @param cb      CriteriaBuilder，用于构造查询条件
     * @param root    Root实体对象，表示查询的根类型
     * @param <T>     实体类型泛型
     * @return Optional封装的Predicate查询条件，若无有效条件则为空
     */
    default <T> Optional<Predicate> parseFilters(String filters, CriteriaBuilder cb, Root<T> root) {
        if (!StringUtils.hasText(filters)) return Optional.empty();
        String[] parts = filters.split(",");
        List<Predicate> predicates = new ArrayList<>();

        for (String part : parts) {
            String[] tokens = part.trim().split(":", 3);
            if (tokens.length != 3) continue;

            String field = tokens[0];
            String op = tokens[1].toLowerCase();
            String value = tokens[2];

            try {
                Path<?> path = root.get(field);
                Object typedValue = convertType(value, path.getJavaType());

                Predicate predicate = null;
                switch (op) {
                    case "eq" -> predicate = cb.equal(path, typedValue);
                    case "ne" -> predicate = cb.notEqual(path, typedValue);
                    case "like" -> predicate = cb.like(path.as(String.class), "%" + value + "%");
                    case "gt", "gte", "lt", "lte" -> {
                        // 类型转换：保证类型安全
                        if (typedValue instanceof Comparable) {
                            @SuppressWarnings("unchecked")
                            Path<Comparable<Object>> cmpPath = (Path<Comparable<Object>>) path;
                            @SuppressWarnings("unchecked")
                            Comparable<Object> cmpValue = (Comparable<Object>) typedValue;

                            predicate = switch (op) {
                                case "gt" -> cb.greaterThan(cmpPath, cmpValue);
                                case "gte" -> cb.greaterThanOrEqualTo(cmpPath, cmpValue);
                                case "lt" -> cb.lessThan(cmpPath, cmpValue);
                                case "lte" -> cb.lessThanOrEqualTo(cmpPath, cmpValue);
                                default -> null;
                            };
                        }
                    }
                }

                if (predicate != null) {
                    predicates.add(predicate);
                }
            } catch (Exception e) {
                throw new RuntimeException("Parse filters error", e);
            }
        }

        return predicates.isEmpty() ? Optional.empty()
                : Optional.of(cb.and(predicates.toArray(new Predicate[0])));
    }

}

