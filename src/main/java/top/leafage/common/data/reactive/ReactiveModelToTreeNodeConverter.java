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

package top.leafage.common.data.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.data.converter.AbstractTreeNodeConverter;
import top.leafage.common.data.domain.TreeNode;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * reactive converter
 *
 * @author wq li
 * @since 0.3.4
 */
public class ReactiveModelToTreeNodeConverter extends AbstractTreeNodeConverter {

    /**
     * Converts a reactive stream of child nodes into a tree structure.
     *
     * @param children a Flux of child nodes.
     * @param <T>      the source type
     * @param <ID>     the pk type
     * @return a Mono emitting the tree node collection.
     * @since 0.2.0
     */
    public static <T, ID> Mono<List<TreeNode<ID>>> toTree(Flux<T> children) {
        return toTree(children, Collections.emptySet());
    }

    /**
     * Converts a reactive stream of child nodes into a tree structure with additional properties.
     *
     * @param children a Flux of child nodes.
     * @param meta     a set of additional properties to include.
     * @param <T>      the source type
     * @param <ID>     the pk type
     * @return a Mono emitting the tree node collection.
     * @since 0.2.0
     */
    public static <T, ID> Mono<List<TreeNode<ID>>> toTree(Flux<T> children, Set<String> meta) {
        return children
                .<TreeNode<ID>>map(child -> createNode(child, meta))
                .collectList()
                .map(AbstractTreeNodeConverter::buildTree);
    }

}
