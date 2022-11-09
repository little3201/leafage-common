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
public abstract class AbstractVO<C> extends BasicVO<C> {

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * modify time getter
     *
     * @return modify time
     */
    public LocalDateTime getModifyTime() {
        return modifyTime;
    }

    /**
     * modify time setter
     *
     * @param modifyTime modify time
     */
    public void setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
    }

}
