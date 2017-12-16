package com.dji.FPVDemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class SettingActivity extends FragmentActivity implements View.OnClickListener {

    private View btnBack;
    private View btnFc;
    private View btnWifi;
    private View btnBattery;
    private View btnUI;
    private View btnCommon;
    private View btnCamera;
    private View btnCompass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);

        btnFc = findViewById(R.id.ll_fc);
        btnFc.setOnClickListener(this);

        btnWifi = findViewById(R.id.ll_wifi);
        btnWifi.setOnClickListener(this);

        btnBattery = findViewById(R.id.ll_battery);
        btnBattery.setOnClickListener(this);

        btnUI = findViewById(R.id.ll_ui);
        btnUI.setOnClickListener(this);

        btnCommon = findViewById(R.id.ll_common);
        btnCommon.setOnClickListener(this);

        btnCamera = findViewById(R.id.ll_camera);
        btnCamera.setOnClickListener(this);

        btnCompass = findViewById(R.id.ll_compass);
        btnCompass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_fc:
                Intent fcIntent = new Intent(this, FCActivity.class);
                startActivity(fcIntent);
                break;
            case R.id.ll_wifi:
                Intent wifiIntent = new Intent(this, WifiActivity.class);
                startActivity(wifiIntent);
                break;
            case R.id.ll_battery:
                Intent batteryIntent = new Intent(this, BatteryActivity.class);
                startActivity(batteryIntent);
                break;
            case R.id.ll_ui:
                Intent uiIntent = new Intent(this, HelmetActivity.class);
                startActivity(uiIntent);
                break;
            case R.id.ll_common:
                Intent commonIntent = new Intent(this, CommonSettingActivity.class);
                startActivity(commonIntent);
                break;
            case R.id.ll_camera:
                Intent cameraIntent = new Intent(this, CameraActivity.class);
                startActivity(cameraIntent);
                break;
            case R.id.ll_compass:
                Intent compassIntent = new Intent(this, CompassActivity.class);
                startActivity(compassIntent);
                break;
        }
    }
}
