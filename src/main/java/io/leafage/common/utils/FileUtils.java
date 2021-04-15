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
     * @param suffix   文件后缀，可传入多类型，使用逗号分隔
     * @return List<File>
     */
    public static List<File> read(String filePath, String suffix) {
        List<File> list = new ArrayList<>();
        File curPath = new File(filePath);
        File[] files = curPath.listFiles();
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }
        String[] suffixes = suffix.split(",");
        for (String curSuffix : suffixes) {
            for (File file : files) {
                if (file.getName().endsWith("." + curSuffix)) {
                    list.add(file);
                }
            }
        }
        return list;
    }

}
