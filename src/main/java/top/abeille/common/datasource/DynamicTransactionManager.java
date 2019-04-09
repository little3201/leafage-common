package top.abeille.common.datasource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * 动态数据源事务管理
 *
 * @author liwenqiang 2019/4/1 16:47
 **/
public class DynamicTransactionManager extends DataSourceTransactionManager {

    /**
     * 只读事务到从库，读写事务到主库
     *
     * @param transaction 事务
     * @param definition  事务释义
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        boolean readOnly = definition.isReadOnly();
        if (readOnly) {
            DataSourceHolder.setDataSource(DataSourceHolder.SLAVE);
        } else {
            DataSourceHolder.setDataSource(DataSourceHolder.MASTER);
        }
        super.doBegin(transaction, definition);
    }

    /**
     * 清理本地线程的数据源
     *
     * @param transaction 事务
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DataSourceHolder.clearDataSource();
    }

}
