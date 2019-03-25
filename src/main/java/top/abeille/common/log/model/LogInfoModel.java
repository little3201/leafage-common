package top.abeille.common.log.model;

import javax.persistence.*;

/**
 * description
 *
 * @author liwenqiang 2019/3/20 19:39
 **/
@Entity
@Table(name = "log_info")
public class LogInfoModel {

    private Integer id;

    /**
     * Get 主键
     */
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    /**
     * Set 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }
}
