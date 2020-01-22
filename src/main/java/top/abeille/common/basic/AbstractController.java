/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

/**
 * 接口基类
 *
 * @author liwenqiang 2018/12/20 9:51
 **/
public abstract class AbstractController {

    /**
     * 开启日志
     */
    protected static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    /**
     * 初始化查询排序规则, 默认以主键倒序排列
     *
     * @param properties 排序字段
     * @return Sort 排序规则配置
     */
    protected Sort initSortProperties(String... properties) {
        if (StringUtils.isEmpty(properties)) {
            logger.info("Sort by pk_id and direction is desc");
            return Sort.by(Sort.Direction.DESC, "id");
        }
        return Sort.by(Sort.Direction.DESC, properties);
    }
}
