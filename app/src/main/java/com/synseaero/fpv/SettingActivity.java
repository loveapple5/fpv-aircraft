package com.synseaero.fpv;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJIWiFiLink;
import dji.sdk.products.DJIAircraft;

public class SettingActivity extends DJIActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.ll_fc).setOnClickListener(this);
        findViewById(R.id.ll_wifi).setOnClickListener(this);
        findViewById(R.id.ll_battery).setOnClickListener(this);
        findViewById(R.id.ll_ui).setOnClickListener(this);
        //findViewById(R.id.ll_common).setOnClickListener(this);
        findViewById(R.id.ll_camera).setOnClickListener(this);
        findViewById(R.id.ll_compass).setOnClickListener(this);
        findViewById(R.id.ll_rc).setOnClickListener(this);
        findViewById(R.id.ll_gimbal).setOnClickListener(this);
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
                DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
                if (djiAircraft != null) {
                    DJIAirLink djiAirLink = djiAircraft.getAirLink();
                    DJIWiFiLink djiWiFiLink = djiAirLink.getWiFiLink();
                    if (djiWiFiLink == null) {
                        Toast.makeText(this, R.string.no_wifi_hint, Toast.LENGTH_SHORT).show();
                    } else {
                        Intent wifiIntent = new Intent(this, WifiActivity.class);
                        startActivity(wifiIntent);
                    }
                }
                break;
            case R.id.ll_battery:
                Intent batteryIntent = new Intent(this, BatteryActivity.class);
                startActivity(batteryIntent);
                break;
            case R.id.ll_ui:
                Intent uiIntent = new Intent(this, HelmetActivity.class);
                startActivity(uiIntent);
                break;
//            case R.id.ll_common:
//                Intent commonIntent = new Intent(this, CommonSettingActivity.class);
//                startActivity(commonIntent);
//                break;
            case R.id.ll_camera:
                Intent cameraIntent = new Intent(this, CameraActivity.class);
                startActivity(cameraIntent);
                break;
            case R.id.ll_compass:
                Intent compassIntent = new Intent(this, CompassActivity.class);
                startActivity(compassIntent);
                break;
            case R.id.ll_rc:
                Intent rcIntent = new Intent(this, RCActivity.class);
                startActivity(rcIntent);
                break;
            case R.id.ll_gimbal:
                Intent gimbalIntent = new Intent(this, GimbalActivity.class);
                startActivity(gimbalIntent);
                break;
        }
    }
}
