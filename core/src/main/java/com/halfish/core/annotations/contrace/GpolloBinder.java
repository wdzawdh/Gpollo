package com.halfish.core.annotations.contrace;

import rx.Subscription;

/**
 * @author cw
 * @date 2017/12/20
 */
public interface GpolloBinder {

    void add(Subscription subscription);

    void unbind();

    boolean isUnbind();
}
