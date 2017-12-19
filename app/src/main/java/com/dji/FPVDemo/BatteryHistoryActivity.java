package com.dji.FPVDemo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import dji.common.battery.DJIBatteryState;
import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.battery.DJIBattery;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class BatteryHistoryActivity extends FragmentActivity implements View.OnClickListener {

    protected static final int MSG_BATTERY_SERIES_NUMBER = 1;
    protected static final int MSG_BATTERY_STATUS = 2;


    private View btnBack;
    private TextView tvBatterySeriesNo;
    private TextView tvBatteryDischargeNum;
    private TextView tvBatteryLiftTime;

    private DJIBattery djiBattery;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BATTERY_SERIES_NUMBER:
                    String batterySerialNumber = msg.getData().getString("batterySerialNumber");
                    tvBatterySeriesNo.setText(batterySerialNumber);
                    break;
                case MSG_BATTERY_STATUS:
                    int dischargeNumber = msg.getData().getInt("dischargeNumber");
                    tvBatteryDischargeNum.setText(dischargeNumber + "次");
                    int batteryLifeTime = msg.getData().getInt("batteryLifeTime");
                    tvBatteryLiftTime.setText(batteryLifeTime + "%");
                    break;
            }
        }

    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_battery_history);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);

        tvBatterySeriesNo = (TextView) findViewById(R.id.tv_battery_no);
        tvBatteryDischargeNum = (TextView) findViewById(R.id.tv_circle_no);
        tvBatteryLiftTime = (TextView) findViewById(R.id.tv_battery_life_time);

        DJIAircraft djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();
        if (djiAircraft != null) {
            djiBattery = djiAircraft.getBattery();
            djiBattery.getSerialNumber(batterySeriesNoCallback);
            djiBattery.setBatteryStateUpdateCallback(new BatteryStateUpdateCallback());

        }
    }

    public void onDestroy() {
        super.onDestroy();
        djiBattery.setBatteryStateUpdateCallback(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    class BatteryStateUpdateCallback implements DJIBattery.DJIBatteryStateUpdateCallback {
        @Override
        public void onResult(DJIBatteryState djiBatteryState) {

            Bundle bundle2 = new Bundle();
            //放电次数
            int dischargeNumber = djiBatteryState.getNumberOfDischarge();
            bundle2.putInt("dischargeNumber", dischargeNumber);


            //电池寿命
            int batteryLifeTime = djiBatteryState.getLifetimeRemainingPercent();
            bundle2.putInt("batteryLifeTime", batteryLifeTime);
            Message msg2 = Message.obtain();
            msg2.what = MSG_BATTERY_STATUS;
            msg2.setData(bundle2);
            handler.sendMessage(msg2);

        }
    }

    DJICommonCallbacks.DJICompletionCallbackWith<String> batterySeriesNoCallback = new DJICommonCallbacks.DJICompletionCallbackWith<String>() {

        @Override
        public void onSuccess(String s) {
            Bundle bundle = new Bundle();
            bundle.putString("batterySerialNumber", s);
            Message msg = Message.obtain();
            msg.what = MSG_BATTERY_SERIES_NUMBER;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    };
}
