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

package top.leafage.common.data.core.scope.enums;

/**
 * Supported configured data scopes.
 *
 * @author wq li
 */
public enum DataScopeType {

    /**
     * No row is visible.
     */
    NONE,

    /**
     * Every row is visible.
     */
    ALL,

    /**
     * Rows owned by the current caller.
     */
    SELF,

    /**
     * Rows owned by the caller's groups.
     */
    GROUP,

    /**
     * Rows owned by the caller's group and its descendants.
     */
    GROUP_TREE,

    /**
     * Rows owned by explicitly configured subjects.
     */
    CUSTOM
}
