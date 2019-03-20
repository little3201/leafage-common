package top.abeille.common.log.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.abeille.common.log.model.LogModel;

/**
 * description
 *
 * @author liwenqiang 2019/3/20 19:38
 **/
public interface LogServerDao extends JpaRepository<LogModel, Long> {
}
