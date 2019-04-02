package top.abeille.common.config.datasource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * 动态数据源事务管理
 *
 * @author liwenqiang 2019/4/1 16:47
 **/
public class DataSourceDynamicTransactionManager extends DataSourceTransactionManager {

    /**
     * 只读事务到从库，读写事务到主库
     *
     * @param transaction
     * @param definition
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        //设置数据源
        boolean readOnly = definition.isReadOnly();
        if (readOnly) {
            DataSourceHolder.putDataSource(DataSourceHolder.SLAVE);
        } else {
            DataSourceHolder.putDataSource(DataSourceHolder.MASTER);
        }
        super.doBegin(transaction, definition);
    }

    /**
     * 清理本地线程的数据源
     *
     * @param transaction
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DataSourceHolder.clearDataSource();
    }

}
