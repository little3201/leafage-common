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

package top.leafage.common.r2dbc;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.TreeAndDomainConverter;
import top.leafage.common.TreeNode;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * r2dbc converter
 *
 * @param <T>  The type of tree node
 * @param <ID> The type of ID
 * @since 0.3.4
 * @author wq li
 */
public abstract class R2dbcTreeAndDomainConverter<T, ID> extends TreeAndDomainConverter<T, ID> {

    /**
     * Converts a r2dbc stream of child nodes into a tree structure.
     *
     * @param children a Flux of child nodes.
     * @return a Mono emitting the tree node collection.
     * @since 0.2.0
     */
    protected Mono<List<TreeNode<ID>>> convertToTree(Flux<T> children) {
        return this.convertToTree(children, Collections.emptySet());
    }

    /**
     * Converts a r2dbc stream of child nodes into a tree structure with additional properties.
     *
     * @param children a Flux of child nodes.
     * @param meta     a set of additional properties to include.
     * @return a Mono emitting the tree node collection.
     * @since 0.2.0
     */
    protected Mono<List<TreeNode<ID>>> convertToTree(Flux<T> children, Set<String> meta) {
        return children
                .map(child -> super.createNode(child, meta))
                .collectList()
                .map(super::buildTree); // 使用父类方法构建树
    }

}
