/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractController
 *
 * @author liwenqiang 2018/12/20 9:51
 **/
public abstract class AbstractController {

    /**
     * 开启日志
     */
    protected static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    /**
     * 初始化分页参数
     * @param pageNum 当前页码
     * @param pageSize 分页大小
     */
    protected void initPageParam(Integer pageNum, Integer pageSize){
        if(null == pageNum){
            pageNum = 0;
        }
        if(null == pageSize){
            pageSize = 10;
        }
    }
}
