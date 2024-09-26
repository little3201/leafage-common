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
 * Represents a tree node structure with a unique identifier, name, parent reference,
 * expandable properties, and child nodes.
 *
 * @author wq li
 * @since 0.1.0
 */
public class TreeNode {

    /**
     * Unique identifier of the node.
     */
    private final Long id;

    /**
     * Name of the node.
     */
    private final String name;

    /**
     * ID of the parent node.
     */
    private final Long superiorId;

    /**
     * Additional properties for extended attributes.
     */
    private final Map<String, Object> expand;

    /**
     * List of child nodes.
     */
    private final List<TreeNode> children;

    /**
     * Private constructor for the TreeNode class. Instances are created via the builder.
     *
     * @param id         The unique identifier of the node.
     * @param name       The name of the node.
     * @param superiorId The parent node ID, or null if this is a root node.
     * @param children   The list of child nodes.
     * @param expand     A map of additional properties for extended attributes.
     */
    private TreeNode(Long id, String name, Long superiorId, List<TreeNode> children, Map<String, Object> expand) {
        this.id = id;
        this.name = name;
        this.superiorId = superiorId;
        this.children = children;
        this.expand = expand;
    }

    /**
     * Starts the builder process with the provided node ID.
     *
     * @param id The unique identifier of the node.
     * @return A TreeNodeBuilder initialized with the provided ID.
     */
    public static TreeNodeBuilder withId(Long id) {
        return builder().id(id);
    }

    /**
     * Creates a new instance of the TreeNodeBuilder for building a TreeNode.
     *
     * @return A new instance of TreeNodeBuilder.
     */
    public static TreeNodeBuilder builder() {
        return new TreeNodeBuilder();
    }

    /**
     * Returns the ID of the parent node.
     *
     * @return Parent node ID.
     */
    public Long getSuperiorId() {
        return superiorId;
    }

    /**
     * Returns the unique identifier of the node.
     *
     * @return Unique identifier of the node.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the name of the node.
     *
     * @return Name of the node.
     */
    public String getName() {
        return name;
    }

    /**
     * builder
     *
     * @author wq li
     * @since 0.3.0
     */
    public static final class TreeNodeBuilder {

        private Long id;

        private String name;

        private Long superiorId;

        private Map<String, Object> expand;

        private List<TreeNode> children;

        private TreeNodeBuilder() {
        }

        /**
         * Sets the node ID for the tree node.
         *
         * @param id The unique identifier of the node.
         * @return The current instance of TreeNodeBuilder.
         */
        public TreeNodeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the name for the tree node.
         *
         * @param name The name of the node.
         * @return The current instance of TreeNodeBuilder.
         */
        public TreeNodeBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the superior ID (superior) for the tree node.
         *
         * @param superiorId The ID of superior node.
         * @return The current instance of TreeNodeBuilder.
         */
        public TreeNodeBuilder superiorId(Long superiorId) {
            this.superiorId = superiorId;
            return this;
        }

        /**
         * Sets additional properties for the tree node.
         *
         * @param expand A map of extended attributes.
         * @return The current instance of TreeNodeBuilder.
         */
        public TreeNodeBuilder expand(Map<String, Object> expand) {
            this.expand = expand;
            return this;
        }

        /**
         * Sets the list of child nodes for the tree node.
         *
         * @param children The list of child nodes.
         * @return The current instance of TreeNodeBuilder.
         */
        public TreeNodeBuilder children(List<TreeNode> children) {
            this.children = children;
            return this;
        }

        /**
         * Builds and returns the TreeNode instance.
         *
         * @return A new TreeNode instance populated with the builder's data.
         */
        public TreeNode build() {
            return new TreeNode(this.id, this.name, this.superiorId, this.children, this.expand);
        }
    }

    /**
     * Returns the expandable properties of the node.
     *
     * @return Map of expandable properties.
     */
    public Map<String, Object> getExpand() {
        return expand;
    }

    /**
     * Returns the list of child nodes.
     *
     * @return List of child nodes.
     */
    public List<TreeNode> getChildren() {
        return children;
    }
}