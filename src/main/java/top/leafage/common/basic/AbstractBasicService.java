package top.leafage.common.basic;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractBasicService {

    private static final List<String> SEED_LIST = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

    /**
     * 获取当前日期的月份和日期，将月份转换为十六进制，拼接日期和四位随机码
     *
     * @return 拼接的编号，例如：20A2103JA
     */
    protected String generateCode() {
        LocalDate localDate = LocalDate.now();
        // 月份转换为十六进制
        return String.valueOf(localDate.getYear()).substring(2) +
                Integer.toHexString(localDate.getMonthValue()).toUpperCase() +
                localDate.getDayOfMonth() +
                generateRandom();
    }

    /**
     * 生成四位随机码
     *
     * @return 组合的四位码，例如：03JA
     */
    private String generateRandom() {
        Collections.shuffle(SEED_LIST);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int nextInt = new SecureRandom().nextInt(SEED_LIST.size());
            sb.append(SEED_LIST.get(nextInt));
        }
        return sb.toString();
    }
}
