/*
 * Copyright(c) 2019-present the original author or authors.
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

package top.leafage.common.data.core;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

/**
 * paging and sort service
 *
 * @author wq li
 */
public interface PagingAndSortService {

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
        if (size > 100) {
            throw new IllegalArgumentException("Page size must be less than 100");
        }

        Sort sort = Sort.by(new Sort.Order(descending ? Sort.Direction.DESC : Sort.Direction.ASC,
                StringUtils.hasText(sortBy) ? sortBy : "id"));

        return PageRequest.of(page, size, sort);
    }

}
