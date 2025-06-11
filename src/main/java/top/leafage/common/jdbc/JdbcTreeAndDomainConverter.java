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

package top.leafage.common.jdbc;

import top.leafage.common.TreeAndDomainConverter;
import top.leafage.common.TreeNode;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * jdbc converter
 *
 * @param <N>  The type of tree node
 * @param <ID> The type of ID
 * @since 0.3.4
 */
public abstract class JdbcTreeAndDomainConverter<N, ID> extends TreeAndDomainConverter<N, ID> {

    /**
     * Converts a list of child nodes into a tree structure.
     *
     * @param children the list of child nodes.
     * @return the tree node collection.
     * @since 0.2.0
     */
    protected List<TreeNode<ID>> convertToTree(List<N> children) {
        return this.convertToTree(children, Collections.emptySet());
    }

    /**
     * Converts a list of child nodes into a tree structure, with additional properties.
     *
     * @param children the list of child nodes.
     * @param meta     a set of additional properties to include.
     * @return the tree node collection.
     * @since 0.2.0
     */
    protected List<TreeNode<ID>> convertToTree(List<N> children, Set<String> meta) {
        List<TreeNode<ID>> nodes = children.stream()
                .map(child -> this.createNode(child, meta))
                .collect(Collectors.toList());

        return this.buildTree(nodes);
    }

}
