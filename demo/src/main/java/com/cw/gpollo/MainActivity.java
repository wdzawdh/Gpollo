package com.cw.gpollo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cw.core.annotations.annotations.Backpressure;
import com.cw.core.annotations.annotations.ObserveOn;
import com.cw.core.annotations.annotations.Receive;
import com.cw.core.annotations.annotations.SubscribeOn;
import com.cw.core.annotations.entity.BackpressureMode;
import com.cw.core.annotations.entity.ThreadMode;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private TextView mTvEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvEvents = findViewById(R.id.tvEvents);
        findViewById(R.id.btAct2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
    }

    @Receive(value = "event1", canNull = true)
    @ObserveOn(ThreadMode.MAIN)
    @SubscribeOn(ThreadMode.MAIN)
    @Backpressure(BackpressureMode.DROP)
    public void onReceive1(Integer i) {
        String s = mTvEvents.getText().toString();
        mTvEvents.setText(s + "\nonReceive1(" + i + ")\n");
    }

    @Receive("event2")
    public void onReceive2(ArrayList<String> event2) {
        String s = mTvEvents.getText().toString();
        mTvEvents.setText(s + "\n" + event2 + "\n");
    }
}
