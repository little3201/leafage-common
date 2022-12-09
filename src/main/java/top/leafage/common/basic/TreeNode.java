package top.leafage.common.basic;

import java.util.List;
import java.util.Map;

/**
 * Tree node
 *
 * @author liwenqiang 2021-07-02 17:18
 */
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
    private Map<String, Object> expand;
    /**
     * 子节点
     */
    private List<TreeNode> children;

    /**
     * 构造方法
     *
     * @param code code
     * @param name name
     */
    public TreeNode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * code getter
     *
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * code setter
     *
     * @param code code
     */
    public void setCode(String code) {
        this.code = code;
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
    public String getSuperior() {
        return superior;
    }

    /**
     * superior setter
     *
     * @param superior superior
     */
    public void setSuperior(String superior) {
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
