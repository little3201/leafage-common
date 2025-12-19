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

package top.leafage.common.data;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

/**
 * abstract service
 *
 * @since 0.4.0
 */
public interface Service {

    /**
     * must not be null
     */
    String _MUST_NOT_BE_NULL = "The given %s must not be null";

    /**
     * must not be empty
     */
    String _MUST_NOT_BE_EMPTY = "The given %s must not be empty";

    /**
     * id must not be null
     */
    String ID_MUST_NOT_BE_NULL = String.format(_MUST_NOT_BE_NULL, "id");


    /**
     * Creates a {@link org.springframework.data.domain.Pageable} object for pagination and sorting.
     *
     * @param page       The page number (zero-based).
     * @param size       The size of the page (number of items per page), capped at 500.
     * @param sortBy     The field to sort by, or null for unsorted pagination.
     * @param descending Whether the sorting should be in descending order.
     * @return A {@link org.springframework.data.domain.Pageable} instance configured with the provided parameters.
     */
    default Pageable pageable(int page, int size, String sortBy, boolean descending) {
        if (size > 500) {
            throw new IllegalArgumentException("Page size must be less than 500");
        }

        Sort sort = Sort.by(new Sort.Order(descending ? Sort.Direction.DESC : Sort.Direction.ASC,
                StringUtils.hasText(sortBy) ? sortBy : "id"));

        return PageRequest.of(page, size, sort);
    }
}
