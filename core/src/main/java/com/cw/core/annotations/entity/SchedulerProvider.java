package com.cw.core.annotations.entity;

import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * @author cw
 * @date 2017/12/21
 */
public class SchedulerProvider {

    private Scheduler main;
    private Scheduler io = Schedulers.io();
    private Scheduler immediate = Schedulers.immediate();
    private Scheduler newThread = Schedulers.newThread();

    private SchedulerProvider(Scheduler main) {
        this.main = main;
    }

    public static SchedulerProvider create(Scheduler main) {
        if (null == main) {
            throw new RuntimeException("the scheduler main must be not null");
        }
        return new SchedulerProvider(main);
    }

    public Scheduler get(ThreadMode mode) {
        switch (mode) {
            case MAIN:
                return main;
            case IO:
                return io;
            case IMMEDIATE:
                return immediate;
            case NEWTHREAD:
                return newThread;
            default:
                return main;
        }
    }
}
