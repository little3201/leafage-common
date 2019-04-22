package top.abeille.common.log.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.abeille.common.log.model.LogInfoModel;

/**
 * 日志操作dao
 *
 * @author liwenqiang 2019/3/20 19:38
 **/
public interface LogInfoDao extends JpaRepository<LogInfoModel, Long> {
}
