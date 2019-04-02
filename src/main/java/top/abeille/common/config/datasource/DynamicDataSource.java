package top.abeille.common.config.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 动态加载数据源
 *
 * @author liwenqiang 2019/4/1 16:43
 **/
public class DynamicDataSource extends AbstractRoutingDataSource {

    private Logger log = LoggerFactory.getLogger(DynamicDataSource.class);

    @Value("${is-poll-read-datasource-pattern}")
    private boolean IS_POLL_READ_DATASOURCE_PATTERN;

    /**
     * 从数据源
     */
    private List<Object> slaveDataSources = new ArrayList<>();

    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    /**
     * 轮询计数
     */
    private AtomicInteger squence = new AtomicInteger(0);

    private static final Long MAX_POOL = Long.MAX_VALUE;

    private final Lock lock = new ReentrantLock();

    public void setSlaveDataSources(List<Object> slaveDataSources) {
        this.slaveDataSources = slaveDataSources;
    }

    @Override
    public void afterPropertiesSet() {
        dataSourceTransactionManager.getDataSource();
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        Object key;
        //主库
        if (DataSourceHolder.isMaster()) {
            key = DataSourceHolder.MASTER;
        } else {
            //从库
            key = this.getSlaveKey(IS_POLL_READ_DATASOURCE_PATTERN);
        }
        log.info("============== current datasource key: {} ==================", key);
        return key;
    }

    /**
     * 轮询获取从库，多个从库使用
     *
     * @param isPollRead 是否轮询获取数据源，false：随机，true：轮询
     * @return slave key
     */
    private Object getSlaveKey(boolean isPollRead) {
        int index;
        if (isPollRead) {
            //轮询方式
            long currValue = squence.incrementAndGet();
            if ((currValue + 1) >= MAX_POOL) {
                try {
                    lock.lock();
                    if ((currValue + 1) >= MAX_POOL) {
                        squence.set(0);
                    }
                } finally {
                    lock.unlock();
                }
            }
            index = (int) (currValue % slaveDataSources.size());
        } else {
            //随机方式
            index = ThreadLocalRandom.current().nextInt(0, slaveDataSources.size());
        }
        return slaveDataSources.get(index);
    }

}
