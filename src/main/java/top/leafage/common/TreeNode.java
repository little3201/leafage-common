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

package top.leafage.common;

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
public class TreeNode {

    /**
     * The unique identifier for the node.
     */
    private final Long id;

    /**
     * The name of the node.
     */
    private final String name;

    /**
     * The ID of the parent node, or null if this node is a root node.
     */
    private final Long superiorId;

    /**
     * A map containing additional, expandable properties of the node.
     */
    private final Map<String, Object> expand;

    /**
     * A list of child nodes. Null or empty if there are no children.
     */
    private final List<TreeNode> children;

    /**
     * Private constructor for the TreeNode class. Instances are created using the {@link TreeNodeBuilder}.
     *
     * @param id         The unique identifier of the node.
     * @param name       The name of the node.
     * @param superiorId The ID of the parent node, or null for a root node.
     * @param children   The list of child nodes. Null or empty if the node has no children.
     * @param expand     A map containing any additional, expandable properties of the node.
     */
    private TreeNode(Long id, String name, Long superiorId, List<TreeNode> children, Map<String, Object> expand) {
        this.id = id;
        this.name = name;
        this.superiorId = superiorId;
        this.children = children;
        this.expand = expand;
    }

    /**
     * Creates a new builder for a TreeNode and initializes it with the provided node ID.
     *
     * @param id The unique identifier for the node.
     * @return A new instance of {@link TreeNodeBuilder}, initialized with the given ID.
     */
    public static TreeNodeBuilder withId(Long id) {
        return builder().id(id);
    }

    /**
     * Creates a new, empty builder for a TreeNode.
     *
     * @return A new instance of {@link TreeNodeBuilder}.
     */
    public static TreeNodeBuilder builder() {
        return new TreeNodeBuilder();
    }

    /**
     * Builder class for creating instances of {@link TreeNode}.
     *
     * <p>Allows for the construction of a {@link TreeNode} by setting properties incrementally.</p>
     *
     * @since 0.3.0
     */
    public static final class TreeNodeBuilder {

        private Long id;
        private String name;
        private Long superiorId;
        private Map<String, Object> expand;
        private List<TreeNode> children;

        /**
         * Private constructor to prevent instantiation.
         */
        private TreeNodeBuilder() {
        }

        /**
         * Sets the node's unique identifier.
         *
         * @param id The node ID.
         * @return The current instance of {@link TreeNodeBuilder}.
         */
        public TreeNodeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the node's name.
         *
         * @param name The node name.
         * @return The current instance of {@link TreeNodeBuilder}.
         */
        public TreeNodeBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the ID of the parent node.
         *
         * @param superiorId The parent node's ID.
         * @return The current instance of {@link TreeNodeBuilder}.
         */
        public TreeNodeBuilder superiorId(Long superiorId) {
            this.superiorId = superiorId;
            return this;
        }

        /**
         * Sets additional expandable properties for the node.
         *
         * @param expand A map of expandable properties.
         * @return The current instance of {@link TreeNodeBuilder}.
         */
        public TreeNodeBuilder expand(Map<String, Object> expand) {
            this.expand = expand;
            return this;
        }

        /**
         * Sets the list of child nodes for the current node.
         *
         * @param children A list of child nodes.
         * @return The current instance of {@link TreeNodeBuilder}.
         */
        public TreeNodeBuilder children(List<TreeNode> children) {
            this.children = children;
            return this;
        }

        /**
         * Constructs and returns a new {@link TreeNode} instance.
         *
         * @return A new instance of {@link TreeNode} with the properties set in the builder.
         */
        public TreeNode build() {
            return new TreeNode(id, name, superiorId, children, expand);
        }
    }


    /**
     * Retrieves the ID of the parent (superior) node, or null if the node is a root node.
     *
     * @return The superior node ID, or null for root nodes.
     */
    public Long getSuperiorId() {
        return superiorId;
    }

    /**
     * Retrieves the unique identifier of the node.
     *
     * @return The node ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Retrieves the name of the node.
     *
     * @return The node name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the map of expandable properties for the node.
     *
     * @return A map containing additional, expandable properties.
     */
    public Map<String, Object> getExpand() {
        return expand;
    }

    /**
     * Retrieves the list of child nodes.
     *
     * @return A list of child nodes, or an empty list if the node has no children.
     */
    public List<TreeNode> getChildren() {
        return children;
    }

}
