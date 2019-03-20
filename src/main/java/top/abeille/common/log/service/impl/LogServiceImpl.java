package top.abeille.common.log.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.abeille.common.log.dao.LogServerDao;
import top.abeille.common.log.model.LogModel;
import top.abeille.common.log.service.LogService;

import java.util.List;

/**
 * description
 *
 * @author liwenqiang 2019/3/20 19:39
 **/
@Service
public class LogServiceImpl implements LogService {

    private final LogServerDao logServerDao;

    @Autowired
    public LogServiceImpl(LogServerDao logServerDao) {
        this.logServerDao = logServerDao;
    }

    @Override
    public void removeById(Long id) {
        logServerDao.deleteById(id);
    }

    @Override
    public void removeInBatch(List<LogModel> entities) {
        logServerDao.deleteAll(entities);
    }

    @Override
    public LogModel save(LogModel entity) {
        return logServerDao.save(entity);
    }
}
