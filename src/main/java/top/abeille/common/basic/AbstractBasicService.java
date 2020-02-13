/*
 * Copyright © 2010-2019 Abeille All rights reserved.
 */

package top.abeille.common.basic;

import java.time.LocalDate;

public class AbstractBasicService {

    /**
     * 获取当前日期的年月日，拼接为字符串
     *
     * @return 拼接的日期，例如：20200213
     */
    protected String getDateValue() {
        LocalDate localDate = LocalDate.now();
        return String.valueOf(localDate.getYear()) + localDate.getMonthValue() + localDate.getDayOfMonth();
    }
}
