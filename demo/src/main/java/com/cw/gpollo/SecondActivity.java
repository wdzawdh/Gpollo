package com.cw.gpollo;

import android.os.Bundle;
import android.view.View;

import com.cw.core.annotations.Gpollo;

import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * @author cw
 * @date 2017/12/21
 */
public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        findViewById(R.id.btSendEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> event2 = new ArrayList<>();
                event2.add("this is event2");

                Gpollo.post("event1", null);
                Gpollo.post("event2", event2);
            }
        });
    }
}
