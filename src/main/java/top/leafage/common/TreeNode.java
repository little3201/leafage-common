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
 * @since 0.3.4
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

    /**
     * <p>withId.</p>
     *
     * @param id  a T object
     * @param <T> a T class
     * @return a {@link top.leafage.common.TreeNode.TreeNodeBuilder} object
     */
    public static <T> TreeNodeBuilder<T> withId(T id) {
        return new TreeNodeBuilder<T>().id(id);
    }

    /**
     * <p>builder.</p>
     *
     * @param <T> a T class
     * @return a {@link top.leafage.common.TreeNode.TreeNodeBuilder} object
     */
    public static <T> TreeNodeBuilder<T> builder() {
        return new TreeNodeBuilder<>();
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a T object
     */
    public T getId() {
        return id;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Getter for the field <code>superiorId</code>.</p>
     *
     * @return a T object
     */
    public T getSuperiorId() {
        return superiorId;
    }

    /**
     * <p>Getter for the field <code>meta</code>.</p>
     *
     * @return a {@link java.util.Map} object
     */
    public Map<String, Object> getMeta() {
        return meta;
    }

    /**
     * <p>Getter for the field <code>children</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<TreeNode<T>> getChildren() {
        return children;
    }

    /**
     * <p>Setter for the field <code>children</code>.</p>
     *
     * @param children a {@link java.util.List} object
     */
    public void setChildren(List<TreeNode<T>> children) {
        this.children = children != null ? children : new ArrayList<>();
    }

    /**
     * TreeNode builder
     *
     * @param <T> type
     */
    public static final class TreeNodeBuilder<T> {
        private T id;
        private String name;
        private T superiorId;
        private Map<String, Object> meta;
        private List<TreeNode<T>> children;

        /**
         * <p>for the field <code>id</code>.</p>
         *
         * @param id node id
         * @return this builder
         */
        public TreeNodeBuilder<T> id(T id) {
            this.id = id;
            return this;
        }

        /**
         * <p>for the field <code>name</code>.</p>
         *
         * @param name node name
         * @return this builder
         */
        public TreeNodeBuilder<T> name(String name) {
            this.name = name;
            return this;
        }

        /**
         * <p>for the field <code>superiorId</code>.</p>
         *
         * @param superiorId node superior id
         * @return this builder
         */
        public TreeNodeBuilder<T> superiorId(T superiorId) {
            this.superiorId = superiorId;
            return this;
        }

        /**
         * <p>for the field <code>meta</code>.</p>
         *
         * @param meta extension fields
         * @return this builder
         */
        public TreeNodeBuilder<T> meta(Map<String, Object> meta) {
            this.meta = meta;
            return this;
        }

        /**
         * <p>for the field <code>children</code>.</p>
         *
         * @param children node children
         * @return this builder
         */
        public TreeNodeBuilder<T> children(List<TreeNode<T>> children) {
            this.children = children;
            return this;
        }

        /**
         * <p>build.</p>
         *
         * @return tree node
         */
        public TreeNode<T> build() {
            return new TreeNode<>(id, name, superiorId, children, meta);
        }
    }
}

