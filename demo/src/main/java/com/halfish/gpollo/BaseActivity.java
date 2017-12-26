package com.halfish.gpollo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.halfish.core.annotations.Gpollo;
import com.halfish.core.annotations.contrace.GpolloBinder;

/**
 * @author cw
 * @date 2017/12/21
 */

public abstract class BaseActivity extends AppCompatActivity {

    private GpolloBinder mBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Gpollo.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Gpollo.unBind(mBind);
    }
}
