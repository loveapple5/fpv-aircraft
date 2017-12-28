package com.synseaero.fpv;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.synseaero.dji.MessageType;

import dji.common.battery.DJIBatteryState;
import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.battery.DJIBattery;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class BatteryHistoryActivity extends DJIActivity implements View.OnClickListener {

    private TextView tvBatterySeriesNo;
    private TextView tvBatteryDischargeNum;
    private TextView tvBatteryLiftTime;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String errDesc = bundle.getString("DJI_DESC", "");
            switch (msg.what) {
                case MessageType.MSG_GET_BATTERY_SERIES_NUMBER_RESPONSE:
                    if (errDesc.isEmpty()) {
                        String batterySerialNumber = msg.getData().getString("batterySerialNumber");
                        tvBatterySeriesNo.setText(batterySerialNumber);
                    } else {
                        Toast.makeText(BatteryHistoryActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                    }

                    break;
                case MessageType.MSG_GET_BATTERY_STATE_RESPONSE:
                    int dischargeNumber = bundle.getInt("dischargeNumber");
                    tvBatteryDischargeNum.setText(dischargeNumber + "次");
                    int batteryLifeTime = bundle.getInt("batteryLifeTime");
                    tvBatteryLiftTime.setText(batteryLifeTime + "%");
                    break;
            }
        }

    };

    private Messenger messenger = new Messenger(handler);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_battery_history);

        findViewById(R.id.iv_back).setOnClickListener(this);

        tvBatterySeriesNo = (TextView) findViewById(R.id.tv_battery_no);
        tvBatteryDischargeNum = (TextView) findViewById(R.id.tv_circle_no);
        tvBatteryLiftTime = (TextView) findViewById(R.id.tv_battery_life_time);

        registerDJIMessenger(MessageType.MSG_GET_BATTERY_STATE_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_BATTERY_SERIES_NUMBER_RESPONSE, messenger);

        sendWatchDJIMessage(MessageType.MSG_WATCH_BATTERY_STATE, 0);

        Message message = Message.obtain();
        message.what = MessageType.MSG_GET_BATTERY_SERIES_NUMBER;
        sendDJIMessage(message);

    }

    public void onDestroy() {
        super.onDestroy();
        unregisterDJIMessenger(MessageType.MSG_GET_BATTERY_STATE_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_BATTERY_SERIES_NUMBER_RESPONSE, messenger);

        sendWatchDJIMessage(MessageType.MSG_WATCH_BATTERY_STATE, 1);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

}
