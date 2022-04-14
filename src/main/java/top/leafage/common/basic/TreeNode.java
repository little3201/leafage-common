package top.leafage.common.basic;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Tree node
 *
 * @author liwenqiang 2021-07-02 17:18
 */
public class TreeNode implements Serializable {

    private static final long serialVersionUID = 3977470984616592112L;

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
    private Map<String, Object> expand;

    private List<TreeNode> children;

    public TreeNode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSuperior() {
        return superior;
    }

    public Map<String, Object> getExpand() {
        return expand;
    }

    public List<TreeNode> getChildren() {
        return children;
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

    public void setExpand(Map<String, Object> expand) {
        this.expand = expand;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

}
