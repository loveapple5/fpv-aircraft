package com.dji.FPVDemo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dji.FPVDemo.view.WaveView;

import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;

public class ConnectActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = ConnectActivity.class.getName();

    private WaveView wcConnect;
    private ImageView ivLauncher;
    private View btnSetting;
    private View btnPrepareFilght;
    private TextView tvModel;


    private Handler handler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_connect);
        ivLauncher = (ImageView) findViewById(R.id.iv_launcher);
        wcConnect = (WaveView) findViewById(R.id.wv_connect);
        wcConnect.setInitialRadius(150);
        wcConnect.setMaxRadiusRate(1);
        wcConnect.setDuration(5000);
        wcConnect.setStyle(Paint.Style.FILL);
        wcConnect.setColor(getResources().getColor(R.color.blue));
        wcConnect.setInterpolator(new LinearOutSlowInInterpolator());
        wcConnect.start();

        btnSetting = findViewById(R.id.tv_setting);
        btnSetting.setOnClickListener(this);

        btnPrepareFilght = findViewById(R.id.tv_prepare_flight);
        btnPrepareFilght.setOnClickListener(this);

        tvModel = (TextView) findViewById(R.id.tv_craft_model);

        handler.postDelayed(hideLauncher, 3000);

        // Register the broadcast receiver for receiving the device connection's changes.
        IntentFilter filter = new IntentFilter();
        filter.addAction(FPVDemoApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
        ((FPVDemoApplication) getApplication()).notifyStatusChange();
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            DJIBaseProduct mProduct = FPVDemoApplication.getProductInstance();

            if (null != mProduct && mProduct.isConnected()) {
                btnSetting.setEnabled(true);
                btnPrepareFilght.setEnabled(true);

                if (null != mProduct.getModel()) {
                    tvModel.setText(mProduct.getModel().getDisplayName());
                }

            } else {
                btnSetting.setEnabled(false);
                btnPrepareFilght.setEnabled(false);
            }
        }
    };

    private Runnable hideLauncher = new Runnable() {
        @Override
        public void run() {
            ivLauncher.setVisibility(View.GONE);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_prepare_flight:

                break;
        }
    }
}
