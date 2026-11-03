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

package top.leafage.common.data.jdbc;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Test entity.
 *
 * @author wq li
 */
@Table("posts")
public class Post {

    @Id
    private Long id;

    private String title;

    /**
     * Creates an empty entity for Spring Data.
     */
    protected Post() {
    }

    /**
     * Creates a post.
     *
     * @param title the title
     */
    public Post(String title) {
        this.title = title;
    }

    /**
     * Returns the identifier.
     *
     * @return the identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }
}
