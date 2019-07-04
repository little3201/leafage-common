/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.datasource;

import org.apache.commons.lang3.StringUtils;

/**
 * 动态数据源操作
 *
 * @author liwenqiang 2019/4/1 16:44
 **/
public final class DataSourceHolder {

    /**
     * 主数据库标识
     */
    public static final String MASTER = "master";

    /**
     * 从数据库标识
     */
    public static final String SLAVER = "slaver";

    /**
     * 本地线程
     */
    private static final ThreadLocal<String> DATASOURCE_HOLDER = new ThreadLocal<>();

    public DataSourceHolder() {
    }

    public static String getDataSource() {
        return DATASOURCE_HOLDER.get();
    }

    public static void setDataSource(String key) {
        if (StringUtils.isNotBlank(key)) {
            DATASOURCE_HOLDER.set(key);
        }
    }

    public static void clearDataSource() {
        DATASOURCE_HOLDER.remove();
    }

    public static boolean isMaster() {
        return getDataSource().equals(MASTER);
    }
}
