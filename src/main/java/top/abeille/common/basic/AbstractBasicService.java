/*
 * Copyright © 2010-2019 Abeille All rights reserved.
 */

package top.abeille.common.basic;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AbstractBasicService {

    /**
     * 获取当前日期的月份和日期，将月份转换为十六进制
     *
     * @return 拼接的日期，例如：20200213
     */
    protected String generateId() {
        LocalDate localDate = LocalDate.now();
        String month = Integer.toHexString(localDate.getMonthValue()).toUpperCase();
        int day = localDate.getDayOfMonth();
        generateRandom();
        return month + day + generateRandom();
    }

    /**
     * 生成四位随机码
     *
     * @return 拼接的日期，例如：20200213
     */
    private String generateRandom() {
        String[] beforeShuffle = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
                "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        List<String> list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (Object o : list) {
            sb.append(o);
        }
        String afterShuffle = sb.toString();
        return afterShuffle.substring(5, 9);
    }
}
