package top.leafage.common.basic;

/**
 * 参数校验信息
 *
 * @author liwenqiang  2022/4/13 17:13
 * @since 0.1.8
 **/
public abstract class ValidMessage {

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
     * code must not be blank
     */
    public static final String CODE_NOT_BLANK = CODE + NOT_BLANK;
    /**
     * name must not be blank
     */
    public static final String NAME_NOT_BLANK = NAME + NOT_BLANK;
    /**
     * username must not be blank
     */
    public static final String USERNAME_NOT_BLANK = USERNAME + NOT_BLANK;
    /**
     * non null
     */
    private static final String NON_NULL = " must not be null.";
    /**
     * code must not be null
     */
    public static final String CODE_NOT_NULL = CODE + NON_NULL;
}
