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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This interface includes methods for creating pageable objects and converting entities.
 *
 * @author wq li
 */
public interface BasicService {

    /**
     * Creates a {@link Pageable} object for pagination and sorting.
     *
     * @param page       The page number (zero-based).
     * @param size       The size of the page (number of items per page), capped at 500.
     * @param sortBy     The field to sort by, or null for unsorted pagination.
     * @param descending Whether the sorting should be in descending order.
     * @return A {@link Pageable} instance configured with the provided parameters.
     */
    default Pageable pageable(int page, int size, String sortBy, boolean descending) {
        size = Math.min(size, 500);

        Sort sort = Sort.by(descending ? Sort.Direction.DESC : Sort.Direction.ASC,
                StringUtils.hasText(sortBy) ? sortBy : "id");

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
     * @throws RuntimeException if the conversion fails.
     */
    default <S, T> T convert(S source, T target) {
        try {
            BeanCopier copier = BeanCopier.create(source.getClass(), target.getClass(), false);
            copier.copy(source, target, null);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Convert error", e);
        }
    }

    /**
     * Converts a source object to an existing target object.
     *
     * @param id               The record id.
     * @param enabled          The record enabled.
     * @param lastModifiedDate The record lastModifiedDate.
     * @param voClass          The class of the target object.
     * @param <T>              The type of the target object.
     * @return The populated target object.
     */
    default <T> T create(Long id, boolean enabled, Instant lastModifiedDate, Class<T> voClass) {
        try {
            Constructor<T> constructor = voClass.getConstructor(Long.class, boolean.class, Instant.class);
            return constructor.newInstance(id, enabled, lastModifiedDate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create VO instance", e);
        }
    }

    /**
     * Parse filters.
     *
     * @param filters filter dsl
     * @param handler handle function
     * @return R
     */
    default <R> Optional<R> parseFilters(String filters, Function<String, R> handler) {
        if (!StringUtils.hasText(filters)) return Optional.empty();
        return Optional.ofNullable(handler.apply(filters));
    }

    /**
     * JPA 查询条件构建（Predicate 构建器）
     */
    default Optional<Predicate> buildJpaPredicate(String filters,
                                                  CriteriaBuilder cb,
                                                  Root<?> root) {
        return parseFilters(filters, raw -> {
            String[] parts = raw.split(",");
            List<Predicate> predicates = new ArrayList<>();

            for (String part : parts) {
                part = part.trim();
                if (part.contains(":")) {
                    String[] kv = part.split(":", 2);
                    predicates.add(cb.equal(root.get(kv[0]), kv[1]));
                } else if (part.contains(">")) {
                    String[] kv = part.split(">", 2);
                    predicates.add(cb.greaterThan(root.get(kv[0]), kv[1]));
                } else if (part.contains("<")) {
                    String[] kv = part.split("<", 2);
                    predicates.add(cb.lessThan(root.get(kv[0]), kv[1]));
                } else if (part.contains("~")) {
                    String[] kv = part.split("~", 2);
                    predicates.add(cb.like(root.get(kv[0]), "%" + kv[1] + "%"));
                }
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    /**
     * R2DBC 查询条件构建（Criteria 构建器）
     */
    default Optional<Criteria> buildR2dbcCriteria(String filters) {
        return parseFilters(filters, raw -> {
            String[] parts = raw.split(",");
            Criteria criteria = null;

            for (String part : parts) {
                part = part.trim();
                Criteria c = null;
                if (part.contains(":")) {
                    String[] kv = part.split(":", 2);
                    c = Criteria.where(kv[0]).is(kv[1]);
                } else if (part.contains(">")) {
                    String[] kv = part.split(">", 2);
                    c = Criteria.where(kv[0]).greaterThan(kv[1]);
                } else if (part.contains("<")) {
                    String[] kv = part.split("<", 2);
                    c = Criteria.where(kv[0]).lessThan(kv[1]);
                } else if (part.contains("~")) {
                    String[] kv = part.split("~", 2);
                    c = Criteria.where(kv[0]).like("%" + kv[1] + "%");
                }
                if (c != null) {
                    criteria = (criteria == null) ? c : criteria.and(c);
                }
            }
            return criteria;
        });
    }
}
