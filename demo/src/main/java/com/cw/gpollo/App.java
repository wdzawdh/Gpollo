package com.cw.gpollo;

import android.app.Application;

import com.cw.core.annotations.Gpollo;

import rx.android.schedulers.AndroidSchedulers;

/**
 * @author cw
 * @date 2017/12/21
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Gpollo.init(AndroidSchedulers.mainThread(), "demo");
    }
}
