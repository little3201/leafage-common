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

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.time.Instant;
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
}
