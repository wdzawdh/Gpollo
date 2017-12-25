package com.halfish.core.annotations.entity;

/**
 * @author cw
 * @date 2017/12/21
 */
public enum ThreadMode {
    /**
     * 事件的处理在和事件的发送在相同的进程
     */
    IMMEDIATE,

    /**
     * 事件的处理开启一个新的线程
     */
    NEWTHREAD,

    /**
     * 事件的处理会在UI线程中执行
     */
    MAIN,

    /**
     * 事件处理会在单独的线程中执行
     */
    IO
}
