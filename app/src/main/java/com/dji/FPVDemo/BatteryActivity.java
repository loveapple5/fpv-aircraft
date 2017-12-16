package com.dji.FPVDemo;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.dji.FPVDemo.util.StringUtils;

import dji.common.battery.DJIBatteryState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.battery.DJIBattery;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;

public class BatteryActivity extends FragmentActivity implements View.OnClickListener {

    protected static final int MSG_BATTERY_STATUS = 1;
    protected static final int MSG_BATTERY_DISCHARGE_TIME = 2;

    private View btnBack;
    private View btnHistory;

    private TextView tvBatteryCurrentEnergy;
    private TextView tvBatteryTemperature;
    private TextView tvFlightTime;
    private EditText etDischargeTime;

    private DJIBattery djiBattery;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BATTERY_STATUS:
                    String BatteryCurrentEnergy = msg.getData().getString("BatteryCurrentEnergy");
                    tvBatteryCurrentEnergy.setText(BatteryCurrentEnergy);

                    String BatteryTemperature = msg.getData().getString("BatteryTemperature");
                    tvBatteryTemperature.setText(BatteryTemperature);
                    break;
                case MSG_BATTERY_DISCHARGE_TIME:
                    int dischargeTime = msg.getData().getInt("BatteryDischargeDay");
                    etDischargeTime.setText(String.valueOf(dischargeTime));
                    break;
            }
        }

    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_battery);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);
        btnHistory = findViewById(R.id.ll_battery_history);
        btnHistory.setOnClickListener(this);

        tvBatteryCurrentEnergy = (TextView) findViewById(R.id.tv_battery_energy);
        tvBatteryTemperature = (TextView) findViewById(R.id.tv_battery_temperature);
        tvFlightTime = (TextView) findViewById(R.id.tv_flight_time);
        etDischargeTime = (EditText) findViewById(R.id.et_discharge_time);
        etDischargeTime.setOnEditorActionListener(etListener);

        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            djiBattery = djiAircraft.getBattery();
            djiBattery.setBatteryStateUpdateCallback(new BatteryStateUpdateCallback());
            DJIFlightController djiFlightController = djiAircraft.getFlightController();
            DJIFlightControllerCurrentState state = djiFlightController.getCurrentState();
            tvFlightTime.setText(StringUtils.getTime(state.getFlightTime()));
            djiBattery.getSelfDischargeDay(dischargeDayCallback);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_battery_history:
                Intent history = new Intent(this, BatteryHistoryActivity.class);
                startActivity(history);
                break;
        }
    }

    class BatteryStateUpdateCallback implements DJIBattery.DJIBatteryStateUpdateCallback {
        @Override
        public void onResult(DJIBatteryState djiBatteryState) {

            Bundle bundle = new Bundle();
            bundle.putString("BatteryCurrentEnergy", djiBatteryState.getCurrentEnergy() + "mAH");

            String temperature = String.format("%.1f", djiBatteryState.getBatteryTemperature());
            bundle.putString("BatteryTemperature", temperature + "℃");
            Message msg = Message.obtain();
            msg.what = MSG_BATTERY_STATUS;
            msg.setData(bundle);
            handler.sendMessage(msg);

        }
    }

    private DJICommonCallbacks.DJICompletionCallbackWith<Integer> dischargeDayCallback = new DJICommonCallbacks.DJICompletionCallbackWith<Integer>() {

        @Override
        public void onSuccess(Integer day) {
            Bundle bundle = new Bundle();
            bundle.putInt("BatteryDischargeDay", day);
            Message msg = Message.obtain();
            msg.what = MSG_BATTERY_DISCHARGE_TIME;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    };

    private TextView.OnEditorActionListener etListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //按下确认的时候进行处理
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                int id = v.getId();
                if (id == R.id.et_discharge_time) {
                    String time = etDischargeTime.getText().toString();
                    djiBattery.setSelfDischargeDay(Integer.valueOf(time), null);
                }
            }
            return false;
        }
    };
}
