/*
 * Copyright © 2010-2019 Everyday Chain. All rights reserved.
 */
package top.abeille.common.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import java.util.Date;

/**
 * 基础信息字段
 *
 * @author liwenqiang  2019-05-29 13:54
 */
public class BasicInfo {

    /**
     * 是否有效
     */
    @JsonIgnore
    @Column(name = "is_enabled")
    private Boolean enabled;
    /**
     * 修改人ID
     */
    @JsonIgnore
    @Column(name = "modifier_id")
    private Long modifierId;
    /**
     * 修改时间
     */
    @JsonIgnore
    @Column(name = "modify_time")
    private Date modifyTime;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getModifierId() {
        return modifierId;
    }

    public void setModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
