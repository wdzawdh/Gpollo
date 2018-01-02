package com.halfish.gpollo;

import android.app.Application;

import com.halfish.core.annotations.Gpollo;

import rx.android.schedulers.AndroidSchedulers;

/**
 * @author cw
 * @date 2017/12/21
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Gpollo.init(AndroidSchedulers.mainThread(), "com.halfish.gpollo");
    }
}
