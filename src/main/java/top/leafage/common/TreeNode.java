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
 * Tree node
 *
 * @author liwenqiang 2021-07-02 17:18
 */
public class TreeNode {

    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 上级
     */
    private Long superior;

    /**
     * 扩展属性
     */
    private Map<String, Object> expand;

    /**
     * 子节点
     */
    private List<TreeNode> children;

    /**
     * 构造方法
     *
     * @param id   主键
     * @param name name
     */
    public TreeNode(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * id getter
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id setter
     *
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * name getter
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * name setter
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * superior getter
     *
     * @return superior
     */
    public Long getSuperior() {
        return superior;
    }

    /**
     * superior setter
     *
     * @param superior superior
     */
    public void setSuperior(Long superior) {
        this.superior = superior;
    }

    /**
     * expand getter
     *
     * @return expand
     */
    public Map<String, Object> getExpand() {
        return expand;
    }

    /**
     * expand setter
     *
     * @param expand expand
     */
    public void setExpand(Map<String, Object> expand) {
        this.expand = expand;
    }

    /**
     * children getter
     *
     * @return children
     */
    public List<TreeNode> getChildren() {
        return children;
    }

    /**
     * children setter
     *
     * @param children children
     */
    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

}
