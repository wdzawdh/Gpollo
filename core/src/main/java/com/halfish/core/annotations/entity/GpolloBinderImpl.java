package com.halfish.core.annotations.entity;

import com.halfish.core.annotations.contrace.GpolloBinder;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author cw
 * @date 2017/12/20
 */
public class GpolloBinderImpl implements GpolloBinder {

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    public void add(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void unbind() {
        if (!mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.clear();
        }
    }

    @Override
    public boolean isUnbind() {
        return mCompositeSubscription.isUnsubscribed();
    }
}
