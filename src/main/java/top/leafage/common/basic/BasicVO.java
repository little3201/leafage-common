package top.leafage.common.basic;

/**
 * 基础VO类
 *
 * @author liwenqiang 2022/7/23 10:04
 * @since 0.1.9
 **/
public class BasicVO<C> {


    /**
     * 代码
     */
    private C code;

    /**
     * 名称
     */
    private String name;

    /**
     * code getter
     *
     * @return C
     */
    public C getCode() {
        return code;
    }

    /**
     * code setter
     *
     * @param code code of model
     */
    public void setCode(C code) {
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
     * @param name name of model
     */
    public void setName(String name) {
        this.name = name;
    }
}
