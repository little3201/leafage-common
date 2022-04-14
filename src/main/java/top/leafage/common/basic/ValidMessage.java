package top.leafage.common.basic;

import java.io.Serializable;

/**
 * 参数校验信息
 *
 * @author liwenqiang  2022/4/13 17:13
 * @since 0.1.8
 **/
public class ValidMessage implements Serializable {

    private static final long serialVersionUID = 3385684846039604779L;

    /**
     * code
     */
    private static final String CODE = "code";

    /**
     * name
     */
    private static final String NAME = "name";

    /**
     * username
     */
    private static final String USERNAME = "username";

    /**
     * not blank
     */
    private static final String NOT_BLANK = " must not be blank.";

    /**
     * non null
     */
    private static final String NON_NULL = " must not be null.";

    /**
     * code must not be blank
     */
    public static final String CODE_NOT_BLANK = CODE + NOT_BLANK;

    /**
     * code must not be null
     */
    public static final String CODE_NOT_NULL = CODE + NON_NULL;

    /**
     * name must not be blank
     */
    public static final String NAME_NOT_BLANK = NAME + NOT_BLANK;

    /**
     * username must not be blank
     */
    public static final String USERNAME_NOT_BLANK = USERNAME + NOT_BLANK;
}
