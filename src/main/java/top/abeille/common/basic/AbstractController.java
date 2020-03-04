/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.basic;

import org.springframework.data.domain.Sort;

import java.util.Objects;

/**
 * 接口基类
 *
 * @author liwenqiang 2018/12/20 9:51
 **/
public abstract class AbstractController {

    /**
     * 初始化查询排序规则, 默认以主键倒序排列
     *
     * @param properties 排序字段
     * @return Sort 排序规则配置
     */
    protected Sort initSortProperties(String... properties) {
        if (Objects.nonNull(properties) && properties.length > 0) {
            return Sort.by(Sort.Direction.DESC, properties);
        }
        return Sort.by(Sort.Direction.DESC, "id");
    }
}
