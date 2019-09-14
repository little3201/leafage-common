/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.mock;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ServiceTest Parent
 *
 * @author liwenqiang 2018/12/28 14:40
 **/
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractServiceMock {

    /**
     * 开启日志
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractServiceMock.class);
}

