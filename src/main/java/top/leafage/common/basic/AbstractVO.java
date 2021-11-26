/*
 * Copyright (c) 2021. Leafage All Right Reserved.
 */
package top.leafage.common.basic;

import java.time.LocalDateTime;

/**
 * Abstract vo
 *
 * @author liwenqiang 2020-10-06 22:09
 * @since 0.1.6
 */
public abstract class AbstractVO<C> {

    /**
     * 代码
     */
    private C code;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    public C getCode() {
        return code;
    }

    public void setCode(C code) {
        this.code = code;
    }

    public LocalDateTime getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
    }
}
