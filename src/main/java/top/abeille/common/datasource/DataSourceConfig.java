package top.abeille.common.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.*;


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
    @ConfigurationProperties("spring.datasource.druid.master")
    public DataSource masterDataSource() {
        return new DruidDataSource();
    }

    /**
     * 从数据源
     *
     * @return Druid数据源
     */
    @Bean(name = "slaveDataSource")
    @ConfigurationProperties("spring.datasource.druid.slave")
    public DataSource slaveDataSource() {
        return new DruidDataSource();
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


    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(@Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
        //需要设置mapper.xml扫描路径配置
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATIONS));
        Properties properties = new Properties();
        properties.setProperty("sqlType", SQL_TYPE);
        sqlSessionFactoryBean.setConfigurationProperties(properties);
        return sqlSessionFactoryBean;
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
