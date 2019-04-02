package top.abeille.common.config.datasource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Properties;

/**
 * description
 *
 * @author liwenqiang 2019/4/2 11:53
 **/
//@Configuration
//@EnableTransactionManagement(proxyTargetClass = true)
public class DataSourceDynamicConfiguration {

    @Value("${sql.type}")
    private String SQL_TYPE;

    @Value("${mybatis.mapperLocations}")
    private String MAPPER_LOCATIONS;

    private final DynamicDataSource dynamicDataSource;

    public DataSourceDynamicConfiguration(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory() throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
        //需要设置mapper.xml扫描路径配置
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATIONS));
        Properties properties = new Properties();
        properties.setProperty("sqlType", SQL_TYPE);
        sqlSessionFactoryBean.setConfigurationProperties(properties);
        return sqlSessionFactoryBean;
    }

    @Bean
    public DataSourceDynamicTransactionManager transactionManager() {
        DataSourceDynamicTransactionManager dynamicManager = new DataSourceDynamicTransactionManager();
        dynamicManager.setDataSource(dynamicDataSource);
        return dynamicManager;
    }
}
