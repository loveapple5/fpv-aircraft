package com.dji.FPVDemo;


import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.WindowManager;

import com.dji.FPVDemo.view.WaveView;

public class ConnectActivity extends FragmentActivity {

    private WaveView wave;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_connect);
        wave = (WaveView) findViewById(R.id.wv_connect);
        wave.setInitialRadius(150);
        wave.setMaxRadiusRate(1);
        wave.setDuration(5000);
        wave.setStyle(Paint.Style.FILL);
        wave.setColor(getResources().getColor(R.color.blue));
        wave.setInterpolator(new LinearOutSlowInInterpolator());
        wave.start();
    }
    
}
