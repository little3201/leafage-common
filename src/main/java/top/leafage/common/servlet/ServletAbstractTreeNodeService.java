/*
 *  Copyright 2018-2022 the original author or authors.
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

package top.leafage.common.servlet;

import top.leafage.common.AbstractTreeNodeService;
import top.leafage.common.TreeNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * For servlet to construct tree
 *
 * @author liwenqiang 2021-07-21 20:08
 * @since 0.1.3
 */
public abstract class ServletAbstractTreeNodeService<T> extends AbstractTreeNodeService<T> {

    /**
     * 处理子节点
     *
     * @param children 子节点
     * @return 树节点数据集
     * @since 0.1.9
     */
    protected List<TreeNode> convert(List<T> children) {
        return this.convert(children, null);
    }

    /**
     * 处理子节点
     *
     * @param children 子节点
     * @param expand   扩展属性
     * @return 树节点数据集
     * @since 0.1.9
     */
    protected List<TreeNode> convert(List<T> children, Set<String> expand) {
        List<TreeNode> nodeList = children.stream().map(child -> this.construct(child, expand)).toList();
        // group by node
        Map<String, List<TreeNode>> listMap = nodeList.stream().filter(node -> Objects.nonNull(node.getSuperior()) &&
                        !"0".equals(node.getSuperior()))
                .collect(Collectors.groupingBy(TreeNode::getSuperior));
        // get children from grouped map
        nodeList.forEach(node -> node.setChildren(listMap.get(node.getCode())));
        return nodeList.stream().filter(node -> Objects.isNull(node.getSuperior()) || "0".equals(node.getSuperior()))
                .collect(Collectors.toList());
    }

}
