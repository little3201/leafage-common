/*
 * Copyright © 2010-2019 Abeille All rights reserved.
 */

package io.leafage.common.basic;

import java.security.SecureRandom;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractBasicService {

    private static final List<String> list = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

    /**
     * 获取当前日期的月份和日期，将月份转换为十六进制，拼接日期和四位随机码
     *
     * @return 拼接的编号，例如：A2103JA
     */
    protected String generateId() {
        LocalDate localDate = LocalDate.now();
        // 月份转换为十六进制
        return Integer.toHexString(localDate.getMonthValue()).toUpperCase() + localDate.getDayOfMonth() + generateRandom();
    }

    /**
     * 生成四位随机码
     *
     * @return 组合的四位码，例如：03JA
     */
    private String generateRandom() {
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int nextInt = new SecureRandom().nextInt(list.size());
            sb.append(list.get(nextInt));
        }
        return sb.toString();
    }

    /**
     * 获取指定日期所在周的最后一天
     *
     * @param localDate 指定日期
     * @return 最后一天的日期
     */
    protected LocalDate lastDayOfWeek(LocalDate localDate) {
        if (null == localDate) {
            localDate = LocalDate.now();
        }
        DayOfWeek week = localDate.getDayOfWeek();
        if (DayOfWeek.SATURDAY.equals(week)) {
            return localDate;
        }
        return LocalDate.from(localDate.plusDays(7 - week.getValue() - 1));
    }

    /**
     * 获取指定日期所在周的第一天
     *
     * @param localDate 指定日期
     * @return 第一天的日期
     */
    protected LocalDate firstDayOfWeek(LocalDate localDate) {
        if (null == localDate) {
            localDate = LocalDate.now();
        }
        DayOfWeek week = localDate.getDayOfWeek();
        if (DayOfWeek.SUNDAY.equals(week)) {
            return localDate;
        }
        return LocalDate.from(localDate.minusDays(7 - (7 - week.getValue())));
    }

    /**
     * 获取指定日期所在月的第一天
     *
     * @param localDate 指定日期
     * @return 第一天的日期
     */
    protected LocalDate firstDayOfMonth(LocalDate localDate) {
        if (null == localDate) {
            localDate = LocalDate.now();
        }
        Month month = localDate.getMonth();
        return LocalDate.of(localDate.getYear(), month, 1);
    }

    /**
     * 获取指定日期所在月的最后一天
     *
     * @param localDate 指定日期
     * @return 最后一天的日期
     */
    protected LocalDate lastDayOfMonth(LocalDate localDate) {
        if (null == localDate) {
            localDate = LocalDate.now();
        }
        Month month = localDate.getMonth();
        return LocalDate.of(localDate.getYear(), month, month.length(localDate.isLeapYear()));
    }

}
