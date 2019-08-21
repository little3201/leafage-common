/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.datasource;

import org.springframework.util.StringUtils;

/**
 * 动态数据源操作
 *
 * @author liwenqiang 2019/4/1 16:44
 **/
public final class DataSourceHolder {

    /**
     * 主数据库标识
     */
    static final String MASTER = "master";

    /**
     * 从数据库标识
     */
    static final String SLAVER = "slaver";

    /**
     * 本地线程
     */
    private static final ThreadLocal<String> DATASOURCE_HOLDER = new ThreadLocal<>();

    public DataSourceHolder() {
    }

    private static String getDataSource() {
        return DATASOURCE_HOLDER.get();
    }

    static void setDataSource(String key) {
        if (!StringUtils.isEmpty(key)) {
            DATASOURCE_HOLDER.set(key);
        }
    }

    static void clearDataSource() {
        DATASOURCE_HOLDER.remove();
    }

    static boolean isMaster() {
        return getDataSource().equals(MASTER);
    }
}
