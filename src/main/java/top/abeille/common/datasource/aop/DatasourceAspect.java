/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.datasource.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.abeille.common.datasource.DataSourceHolder;

/**
 * description
 *
 * @author liwenqiang 2019/4/3 10:52
 **/
@Aspect
@Component
public class DatasourceAspect {

    private Logger log = LoggerFactory.getLogger(DatasourceAspect.class);

    @Pointcut("@annotation(top.abeille.common.datasource.aop.Slave)")
    public void readPointcut() {
    }

    @Pointcut("@annotation(top.abeille.common.datasource.aop.Master)")
    public void writePointcut() {
    }


    @Before("readPointcut()")
    public void setReadAspect() {
        DataSourceHolder.setDataSource(DataSourceHolder.SLAVE);
        log.info("dataSource切换到：slave");
    }

    @Before("writePointcut()")
    public void setWriteAspect() {
        DataSourceHolder.setDataSource(DataSourceHolder.MASTER);
        log.info("dataSource切换到：slave");
    }


}
