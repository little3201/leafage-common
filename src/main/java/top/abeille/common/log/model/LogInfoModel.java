/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.log.model;

import javax.persistence.*;
import java.util.Date;

/**
 * description
 *
 * @author liwenqiang 2019/3/20 19:39
 **/
@Entity
@Table(name = "log_info")
public class LogInfoModel {

    /**
     * 主键
     */
    private Long id;
    /**
     * ip地址
     */
    private String ip;
    /**
     * 操作内容
     */
    private String operation;
    /**
     * 参数
     */
    private String param;
    /**
     * 操作用户ID
     */
    private Long modifierId;
    /**
     * 操作时间
     */
    private Date modifyTime;

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Column(name = "operation")
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Column(name = "param")
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Column(name = "modifier_id")
    public Long getModifierId() {
        return modifierId;
    }

    public void setModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    @Column(name = "modify_time")
    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
