/*
 * Copyright (c) 2024.  little3201.
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
 *
 */

package top.leafage.common;

import java.time.Instant;

public interface ReadonlyMetadata {

    /**
     * Get the unique identifier.
     *
     * @return the template ID
     */
    Long getId();

    /**
     * Checks if the template is enabled.
     *
     * @return true if the template is enabled, false otherwise
     */
    boolean isEnabled();

    /**
     * Gets the timestamp of the last modification.
     *
     * @return the last modified date
     */
    Instant getLastModifiedDate();
}
