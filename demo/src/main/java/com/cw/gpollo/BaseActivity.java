package com.cw.gpollo;

import android.os.Bundle;

import com.cw.core.annotations.Gpollo;
import com.cw.core.annotations.contrace.GpolloBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
