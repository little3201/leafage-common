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
 * @author liwenqiang
 * @since 2021-07-02
 */
public class TreeNode {

    /**
     * Unique identifier of the node.
     */
    private Long id;

    /**
     * Name of the node.
     */
    private String name;

    /**
     * ID of the parent node.
     */
    private Long superior;

    /**
     * Additional properties for extended attributes.
     */
    private Map<String, Object> expand;

    /**
     * List of child nodes.
     */
    private List<TreeNode> children;

    /**
     * Constructor for creating a tree node with the specified ID and name.
     *
     * @param id   Unique identifier of the node.
     * @param name Name of the node.
     */
    public TreeNode(Long id, String name) {
        this.id = id;
        this.name = name;
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
     * Sets the unique identifier of the node.
     *
     * @param id Unique identifier to be set.
     */
    public void setId(Long id) {
        this.id = id;
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
     * Sets the name of the node.
     *
     * @param name Name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the ID of the parent node.
     *
     * @return Parent node ID.
     */
    public Long getSuperior() {
        return superior;
    }

    /**
     * Sets the ID of the parent node.
     *
     * @param superior Parent node ID to be set.
     */
    public void setSuperior(Long superior) {
        this.superior = superior;
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
     * Sets the expandable properties of the node.
     *
     * @param expand Map of properties to be set.
     */
    public void setExpand(Map<String, Object> expand) {
        this.expand = expand;
    }

    /**
     * Returns the list of child nodes.
     *
     * @return List of child nodes.
     */
    public List<TreeNode> getChildren() {
        return children;
    }

    /**
     * Sets the list of child nodes.
     *
     * @param children List of child nodes to be set.
     */
    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }
}


