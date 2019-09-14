/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * 开启日志
     */
    private Logger log = LoggerFactory.getLogger(DynamicDataSource.class);
    /**
     * 连接池最大限度
     */
    private static final Long MAX_POOL = 8L;
    /**
     * 锁定义
     */
    private final Lock lock = new ReentrantLock();
    /**
     * 从数据源
     */
    private List<Object> slaverDataSources = new ArrayList<>();
    /**
     * 轮询计数
     */
    private AtomicInteger sequence = new AtomicInteger(0);
    private Boolean isPollRead = false;

    void setSlaveDataSources(List<Object> slaverDataSources) {
        this.slaverDataSources = slaverDataSources;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        Object key;
        if (DataSourceHolder.isMaster()) {
            key = DataSourceHolder.MASTER;
        } else {
            setPollRead(true);
            key = this.getSlaverKey(isPollRead());
        }
        log.info("============== current datasource key: {} ==============", key);
        return key;
    }

    public boolean isPollRead() {
        return isPollRead;
    }

    public void setPollRead(boolean pollRead) {
        isPollRead = pollRead;
    }

    /**
     * 轮询获取从库，多个从库使用
     *
     * @param isPollRead 是否轮询获取数据源，默认随机，false：随机，true：轮询
     * @return slave key
     */
    private Object getSlaverKey(boolean isPollRead) {
        int index;
        if (isPollRead) {
            long currValue = sequence.incrementAndGet();
            if (MAX_POOL <= (currValue + 1)) {
                try {
                    lock.lock();
                    if (MAX_POOL <= (currValue + 1)) {
                        sequence.set(0);
                    }
                } finally {
                    lock.unlock();
                }
            }
            index = (int) (currValue % slaverDataSources.size());
        } else {
            index = ThreadLocalRandom.current().nextInt(0, slaverDataSources.size());
        }
        return slaverDataSources.get(index);
    }

}
