package com.guagusi.temple;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.guagusi.temple.musenotificationbus.MuseNotificationCenter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mBtnRegister;
    private Button mBtnUnregister;
    private TextView mTexVMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(this);
        mBtnUnregister = (Button) findViewById(R.id.btn_unregister);
        mBtnUnregister.setOnClickListener(this);
        mTexVMsg = (TextView) findViewById(R.id.texV);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinkedHashMap paramMap = new LinkedHashMap();
                paramMap.put("what", 2);
                paramMap.put("msg", "message");
                List list = new ArrayList<>();
                list.add("good");
                list.add("day");
                paramMap.put("data", list);
                MuseNotificationCenter.instanceMuse().postNotification("test", paramMap);
            }

        });
    }

    public void onMessage(int what, String msg, ArrayList data) {
        Log.e("MuseNotificationCenter", "============== onMessage on callback " + data.get(0));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if(v == mBtnRegister) {
            LinkedHashMap<String, Class> paramMap = new LinkedHashMap();
            paramMap.put("what", Integer.class);
            paramMap.put("msg", String.class);

            paramMap.put("data", List.class);
            // 监听对象，观察主题，优先级，回调方法，回调方法参数
            MuseNotificationCenter.instanceMuse().registerObserver(this, "test", 1, "onMessage", paramMap);
        } else if(v == mBtnUnregister) {
            MuseNotificationCenter.instanceMuse().unregisterObserver(this);
        }
    }
}
