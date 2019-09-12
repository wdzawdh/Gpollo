package com.cw.core.annotations.entity;

/**
 * @author cw
 * @date 2018/3/23
 */

public enum BackpressureMode {
    /**
     * 事件做缓存
     */
    BUFFER,

    /**
     * 事件抛弃掉
     */
    DROP,

    /**
     * 默认
     */
    NORMAL
}
