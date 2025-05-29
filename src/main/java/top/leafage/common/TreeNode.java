/*
 *  Copyright 2018-2025 little3201.
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

package top.leafage.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a tree node structure, which includes a unique identifier (ID),
 * a name, an optional parent (superior) ID, expandable properties, and child nodes.
 * This class provides a flexible structure for hierarchical data representation.
 *
 * <p>This class is designed to be immutable and is constructed using a builder pattern.
 * The builder allows for easy configuration of all properties before creating an instance.</p>
 *
 * @author wq
 * @since 0.1.0
 */
public class TreeNode<T> {

    private final T id;
    private final String name;
    private final T superiorId;
    private final Map<String, Object> meta;
    private List<TreeNode<T>> children;

    private TreeNode(T id, String name, T superiorId, List<TreeNode<T>> children, Map<String, Object> meta) {
        this.id = id;
        this.name = name;
        this.superiorId = superiorId;
        this.meta = meta != null ? meta : Collections.emptyMap();
        this.children = children != null ? children : new ArrayList<>();
    }

    public static <T> TreeNodeBuilder<T> withId(T id) {
        return new TreeNodeBuilder<T>().id(id);
    }

    public static <T> TreeNodeBuilder<T> builder() {
        return new TreeNodeBuilder<>();
    }

    public T getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public T getSuperiorId() {
        return superiorId;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children != null ? children : new ArrayList<>();
    }

    public static final class TreeNodeBuilder<T> {
        private T id;
        private String name;
        private T superiorId;
        private Map<String, Object> meta;
        private List<TreeNode<T>> children;

        public TreeNodeBuilder<T> id(T id) {
            this.id = id;
            return this;
        }

        public TreeNodeBuilder<T> name(String name) {
            this.name = name;
            return this;
        }

        public TreeNodeBuilder<T> superiorId(T superiorId) {
            this.superiorId = superiorId;
            return this;
        }

        public TreeNodeBuilder<T> meta(Map<String, Object> meta) {
            this.meta = meta;
            return this;
        }

        public TreeNodeBuilder<T> children(List<TreeNode<T>> children) {
            this.children = children;
            return this;
        }

        public TreeNode<T> build() {
            return new TreeNode<>(id, name, superiorId, children, meta);
        }
    }
}

