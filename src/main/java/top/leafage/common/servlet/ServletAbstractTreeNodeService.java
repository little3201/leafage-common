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

package top.leafage.common.servlet;

import top.leafage.common.AbstractTreeNodeService;
import top.leafage.common.TreeNode;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Abstract service for constructing a tree structure in a servlet context.
 *
 * @param <T> the type of nodes in the tree
 * @author wq li
 * @since 0.1.3
 */
public abstract class ServletAbstractTreeNodeService<T> extends AbstractTreeNodeService<T> {

    /**
     * Converts a list of child nodes into a tree structure.
     *
     * @param children the list of child nodes.
     * @return the tree node collection.
     * @since 0.2.0
     */
    protected List<TreeNode> convertToTree(List<T> children) {
        return convertToTree(children, Collections.emptySet());
    }

    /**
     * Converts a list of child nodes into a tree structure, with additional properties.
     *
     * @param children the list of child nodes.
     * @param meta     a set of additional properties to include.
     * @return the tree node collection.
     * @since 0.2.0
     */
    protected List<TreeNode> convertToTree(List<T> children, Set<String> meta) {
        return children(children.stream()
                .map(child -> createNode(child, meta))
                .toList());
    }
}

