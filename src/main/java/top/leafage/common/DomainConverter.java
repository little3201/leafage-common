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

/**
 * This interface includes methods for creating pageable objects and converting entities.
 *
 * @author wq li
 * @since 0.3.4
 */
public abstract class DomainConverter {

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

}
