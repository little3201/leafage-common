package top.abeille.common.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
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

    @Bean(name = "masterEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mssqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean em = builder
                .dataSource(masterDataSource())
//                .packages(OldBuy.class)
                .persistenceUnit(DataSourceHolder.MASTER)
                .build();
        Properties properties = new Properties();
        properties.setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        em.setJpaProperties(properties);
        return em;
    }

    @Bean(name = "slaveEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean em = builder
                .dataSource(slaveDataSource())
//                .packages(Buy.class)
                .persistenceUnit(DataSourceHolder.SLAVE)
                .build();
        Properties properties = new Properties();
        properties.setProperty("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        em.setJpaProperties(properties);
        return em;
    }

    @Primary
    @Bean(name = "dynamicEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory() {

        return null;
    }


    @Bean(name = "masterTransactionManager")
    PlatformTransactionManager masterTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(mssqlEntityManagerFactory(builder).getObject());
    }

    @Bean(name = "slaveTransactionManager")
    PlatformTransactionManager slaveTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(mysqlEntityManagerFactory(builder).getObject());
    }

    @Bean(name = "dynamicTransactionManager")
    PlatformTransactionManager dynamicTransactionManager(EntityManagerFactoryBuilder builder) {
        return null;
    }
}
