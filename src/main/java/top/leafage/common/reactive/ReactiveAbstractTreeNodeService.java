/*
 *  Copyright 2018-2024 little3201.
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

package top.leafage.common.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.AbstractTreeNodeService;
import top.leafage.common.TreeNode;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Abstract service for constructing a reactive tree structure.
 *
 * @param <T> the type of nodes in the tree
 * @author wq li
 * @since 0.1.3
 */
public abstract class ReactiveAbstractTreeNodeService<T> extends AbstractTreeNodeService<T> {

    /**
     * Converts a reactive stream of child nodes into a tree structure.
     *
     * @param children a Flux of child nodes
     * @return a Mono emitting the tree node collection
     * @since 0.2.0
     */
    protected Mono<List<TreeNode>> convert(Flux<T> children) {
        return this.convert(children, Collections.emptySet());
    }

    /**
     * Converts a reactive stream of child nodes into a tree structure with additional properties.
     *
     * @param children a Flux of child nodes
     * @param meta   a set of additional properties to include
     * @return a Mono emitting the tree node collection
     * @since 0.2.0
     */
    protected Mono<List<TreeNode>> convert(Flux<T> children, Set<String> meta) {
        return children
                .map(child -> this.createNode(child, meta))
                .collectList()
                .map(this::children);
    }
}
