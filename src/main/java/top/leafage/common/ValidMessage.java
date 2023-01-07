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

package top.leafage.common;

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
