/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 数据源配置
 *
 * @author liwenqiang 2019/4/1 15:53
 **/
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@EnableTransactionManagement(proxyTargetClass = true)
class DataSourceConfig {

    /**
     * 主数据源
     *
     * @return 主数据源
     */
    @Primary
    @Bean(name = "masterDataSource")
    @ConfigurationProperties("spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 从数据源
     *
     * @return 从数据源
     */
    @Bean(name = "slaverDataSource")
    @ConfigurationProperties("spring.datasource.slaver")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }


    /**
     * 构建动态数据源
     *
     * @param masterDataSource 主数据源
     * @param slaveDataSource  从数据源
     * @return 动态数据源
     */
    @Bean(name = "dynamicDataSource")
    public DynamicDataSource dynamicDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                               @Qualifier("slaverDataSource") DataSource slaveDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>(16);

        targetDataSources.put("master", masterDataSource);
        targetDataSources.put("slaver", slaveDataSource);
        dynamicDataSource.setTargetDataSources(targetDataSources);

        List<Object> slaveDataSources = new ArrayList<>();
        slaveDataSources.add("slaver");

        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        dynamicDataSource.setSlaveDataSources(slaveDataSources);

        return dynamicDataSource;

    }

    /**
     * 事务管理
     *
     * @param dataSource 动态数据源
     * @return 事务管理器
     */
    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dynamicDataSource") DynamicDataSource dataSource) {
        DynamicTransactionManager dynamicDataSourceTransactionManager = new DynamicTransactionManager();
        dynamicDataSourceTransactionManager.setDataSource(dataSource);
        return dynamicDataSourceTransactionManager;
    }
}
