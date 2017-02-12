package com.dji.FPVDemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.TextView;

import dji.common.battery.DJIBatteryState;
import dji.sdk.battery.DJIBattery;

public class StatusListActivity extends Activity {

    protected static final int CHANGE_BATTERY_STATUS = 0;

    private DJIBattery djiBattery;

    private TextView tvBatteryStatus;

    protected Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_BATTERY_STATUS:
                    String battery = msg.getData().getString("battery");
                    tvBatteryStatus.setText(battery);
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏
        setContentView(R.layout.status_list);
        initUI();
        initDji();
    }

    private void initUI() {
        tvBatteryStatus = (TextView) findViewById(R.id.tv_battery_voltage);
    }

    private void initDji() {
        djiBattery = FPVDemoApplication.getProductInstance().getBattery();
        BatteryStateUpdateCallback batteryCallback = new BatteryStateUpdateCallback();
        djiBattery.setBatteryStateUpdateCallback(batteryCallback);
    }

    class BatteryStateUpdateCallback implements DJIBattery.DJIBatteryStateUpdateCallback {
        @Override
        public void onResult(DJIBatteryState djiBatteryState) {
            StringBuffer stringBuffer = new StringBuffer();
            //stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append(djiBatteryState.getBatteryEnergyRemainingPercent()).append("%");
//                stringBuffer.append("CurrentVoltage: ").
//                        append(djiBatteryState.getCurrentVoltage()).append("mV\n");
//                stringBuffer.append("CurrentCurrent: ").
//                        append(djiBatteryState.getCurrentCurrent()).append("mA\n");
            Bundle bundle = new Bundle();
            bundle.putString("battery", stringBuffer.toString());
            Message msg = Message.obtain();
            msg.what = CHANGE_BATTERY_STATUS;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }
}
