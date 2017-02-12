package com.dji.FPVDemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class StatusListActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏
        setContentView(R.layout.status_list);
    }
}
