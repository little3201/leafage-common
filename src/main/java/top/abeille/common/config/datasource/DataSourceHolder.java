package top.abeille.common.config.datasource;

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
    public static final String SLAVE = "slave";

    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    public DataSourceHolder() {
    }

    public static void putDataSource(String key) {
        holder.set(key);
    }

    public static String getDataSource() {
        return holder.get();
    }

    public static void clearDataSource() {
        holder.remove();
    }

    public static boolean isMaster() {
        return getDataSource().equals(MASTER);
    }
}
