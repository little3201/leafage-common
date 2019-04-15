package top.abeille.common.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
 * description
 *
 * @author liwenqiang 2019/4/1 15:53
 **/
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class DataSourceConfig {

    @Value("${datasource.sql.type}")
    private String SQL_TYPE;

    @Value("${mybatis.mapperLocations}")
    private String MAPPER_LOCATIONS;

    /**
     * 主数据源
     *
     * @return Druid数据源
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
     * @return Druid数据源
     */
    @Bean(name = "slaveDataSource")
    @ConfigurationProperties("spring.datasource.slave")
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
                                               @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();

        targetDataSources.put("master", masterDataSource);
        targetDataSources.put("slave", slaveDataSource);
        dynamicDataSource.setTargetDataSources(targetDataSources);

        List<Object> slaveDataSources = new ArrayList<>();
        slaveDataSources.add("slave");

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
