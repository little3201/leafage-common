package top.leafage.common.basic;

import java.util.List;
import java.util.Map;

public class TreeNode {

    /**
     * 代码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 上级
     */
    private String superior;
    /**
     * 扩展属性
     */
    private Map<String, String> expand;

    private List<TreeNode> children;

    public TreeNode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSuperior(String superior) {
        this.superior = superior;
    }

    public void setExpand(Map<String, String> expand) {
        this.expand = expand;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

}
