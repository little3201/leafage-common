/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.log.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.abeille.common.log.dao.LogInfoDao;
import top.abeille.common.log.model.LogInfoModel;
import top.abeille.common.log.service.LogInfoService;

import java.util.List;

/**
 * description
 *
 * @author liwenqiang 2019/3/20 19:39
 **/
@Service
public class LogInfoServiceImpl implements LogInfoService {

    private final LogInfoDao logInfoDao;

    @Autowired
    public LogInfoServiceImpl(LogInfoDao logInfoDao) {
        this.logInfoDao = logInfoDao;
    }

    @Override
    public void removeById(Long id) {
        logInfoDao.deleteById(id);
    }

    @Override
    public void removeInBatch(List<LogInfoModel> entities) {
        logInfoDao.deleteAll(entities);
    }

    @Override
    public LogInfoModel save(LogInfoModel entity) {
        return logInfoDao.save(entity);
    }
}
