/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

/**
 * 接口基类
 *
 * @author liwenqiang 2018/12/20 9:51
 **/
public abstract class AbstractController {

    /**
     * 日志
     */
    protected static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    /**
     * 初始化分页参数, 默认以主键倒序排列
     *
     * @param pageNum  当前页
     * @param pageSize 分页大小，默认10
     * @return Pageable 分页配置
     */
    protected Pageable initPageParams(Integer pageNum, Integer pageSize) {
        if (Objects.isNull(pageNum) || pageNum < 0) {
            pageNum = 0;
        }
        if (Objects.isNull(pageSize) || pageSize < 0) {
            pageSize = 10;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return PageRequest.of(pageNum, pageSize, sort);
    }
}
