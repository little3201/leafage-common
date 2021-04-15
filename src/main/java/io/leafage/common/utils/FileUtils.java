/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package io.leafage.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文件操作工具类
 *
 * @author liwenqiang 2019/3/25 14:43
 **/
public class FileUtils {

    private FileUtils() {
    }

    /**
     * 根据路径，后缀，获取指定路径下的文件
     *
     * @param filePath 读取路径
     * @param suffix   文件后缀，可传入多类型，逗号分隔
     * @return List<File>
     */
    public static List<File> read(String filePath, String suffix) {
        File curPath = new File(filePath);
        File[] files = curPath.listFiles();
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }
        List<File> fileList = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                String[] suffixes = suffix.split(",");
                for (String curSuffix : suffixes) {
                    if (file.getName().endsWith("." + curSuffix.toUpperCase()) ||
                            file.getName().endsWith("." + curSuffix.toLowerCase())) {
                        fileList.add(file);
                    }
                }
            } else if (file.isDirectory()) {
                List<File> list = read(file.getPath(), suffix);
                fileList.addAll(list);
            }
        }
        return fileList;
    }

    /**
     * 获取字符串中第一个指定字符类型的位置
     * <p>
     * eg: 获取第一个中文的位置, scriptIndex("001目标", Character.UnicodeScript.HAN), return 3
     *
     * @param splitStr 要截取的字符串
     * @return 一个指定字符类型在字符串中的位置
     */
    private static int scriptIndex(String splitStr, Character.UnicodeScript unicodeScript) {
        char[] chars = splitStr.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            Character.UnicodeScript sc = Character.UnicodeScript.of(chars[i]);
            if (sc == unicodeScript) {
                return i;
            }
        }
        return 0;
    }

}
