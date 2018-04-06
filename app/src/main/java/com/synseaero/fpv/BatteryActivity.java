package com.synseaero.fpv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.synseaero.dji.MessageType;
import com.synseaero.util.DJIUtils;
import com.synseaero.util.StringUtils;

public class BatteryActivity extends DJIActivity implements View.OnClickListener {

    private TextView tvBatteryCurrentEnergy;
    private TextView tvBatteryTemperature;
    private TextView tvFlightTime;
    //private EditText etDischargeTime;
    private TextView tvLowEnergy;
    private SeekBar sbLowEnergy;

    private final static int MIN_LOW_ENERGY = 15;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String errDesc = bundle.getString("DJI_DESC", "");
            switch (msg.what) {
                case MessageType.MSG_GET_BATTERY_STATE_RESPONSE:

                    int currentEnergy = bundle.getInt("currentEnergy", 0);
                    float batteryTemperature = bundle.getFloat("batteryTemperature", 0);

                    String temperature = String.format("%.1f℃", batteryTemperature);
                    tvBatteryCurrentEnergy.setText(currentEnergy + "mAH");
                    tvBatteryTemperature.setText(temperature);

                    break;
//                case MessageType.MSG_GET_BATTERY_DISCHARGE_DAY_RESPONSE:
//
//                    if (errDesc.isEmpty()) {
//                        int dischargeDay = bundle.getInt("day", 0);
//                        //etDischargeTime.setText(String.valueOf(dischargeDay));
//                    } else {
//                        Toast.makeText(BatteryActivity.this, errDesc, Toast.LENGTH_SHORT).show();
//                    }
//
//                    break;
//                case MessageType.MSG_SET_BATTERY_DISCHARGE_DAY_RESPONSE:
//
//                    if (!errDesc.isEmpty()) {
//                        Toast.makeText(BatteryActivity.this, errDesc, Toast.LENGTH_SHORT).show();
//                    }
//
//                    break;
                case MessageType.MSG_GET_FC_STATE_RESPONSE:

                    int flightTimeSec = bundle.getInt("flightTime", 0);
                    tvFlightTime.setText(StringUtils.getTime(flightTimeSec));
                    break;
            }
        }

    };

    private Messenger messenger = new Messenger(handler);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_battery);

        findViewById(R.id.iv_back).setOnClickListener(this);
        //findViewById(R.id.ll_battery_history).setOnClickListener(this);

        tvBatteryCurrentEnergy = (TextView) findViewById(R.id.tv_battery_energy);
        tvBatteryTemperature = (TextView) findViewById(R.id.tv_battery_temperature);
        tvFlightTime = (TextView) findViewById(R.id.tv_flight_time);
//        etDischargeTime = (EditText) findViewById(R.id.et_discharge_time);
//        etDischargeTime.setOnEditorActionListener(etListener);

        tvLowEnergy = (TextView) findViewById(R.id.tv_low_energy_warning);
        sbLowEnergy = (SeekBar) findViewById(R.id.sb_low_energy_warning);
        sbLowEnergy.setOnSeekBarChangeListener(new LowEnergyListener());

        registerDJIMessenger(MessageType.MSG_GET_BATTERY_STATE_RESPONSE, messenger);
        //registerDJIMessenger(MessageType.MSG_GET_BATTERY_DISCHARGE_DAY_RESPONSE, messenger);
        //registerDJIMessenger(MessageType.MSG_SET_BATTERY_DISCHARGE_DAY_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_FC_STATE_RESPONSE, messenger);

        sendWatchDJIMessage(MessageType.MSG_WATCH_BATTERY_STATE, 0);

//        Message getDischargeMsg = Message.obtain();
//        getDischargeMsg.what = MessageType.MSG_GET_BATTERY_DISCHARGE_DAY;
//        sendDJIMessage(getDischargeMsg);

        Message getFlightStateMsg = Message.obtain();
        getFlightStateMsg.what = MessageType.MSG_GET_FC_STATE;
        sendDJIMessage(getFlightStateMsg);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("battery", Context.MODE_PRIVATE);
        int lowEnergyWarningThreshold = sp.getInt("lowEnergyWarningThreshold", DJIUtils.COMMON_LOW_PERCENT);
        if(lowEnergyWarningThreshold < MIN_LOW_ENERGY) {
            lowEnergyWarningThreshold = MIN_LOW_ENERGY;
        }
        tvLowEnergy.setText(String.valueOf(lowEnergyWarningThreshold));
        sbLowEnergy.setProgress(lowEnergyWarningThreshold - MIN_LOW_ENERGY);
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterDJIMessenger(MessageType.MSG_GET_BATTERY_STATE_RESPONSE, messenger);
        //unregisterDJIMessenger(MessageType.MSG_GET_BATTERY_DISCHARGE_DAY_RESPONSE, messenger);
        //unregisterDJIMessenger(MessageType.MSG_SET_BATTERY_DISCHARGE_DAY_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_FC_STATE_RESPONSE, messenger);

        sendWatchDJIMessage(MessageType.MSG_WATCH_BATTERY_STATE, 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
//            case R.id.ll_battery_history:
//                Intent history = new Intent(this, BatteryHistoryActivity.class);
//                startActivity(history);
//                break;
        }
    }

//    private TextView.OnEditorActionListener etListener = new TextView.OnEditorActionListener() {
//
//        @Override
//        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//            //按下确认的时候进行处理
//            if (EditorInfo.IME_ACTION_DONE == actionId) {
//                int id = v.getId();
//                if (id == R.id.et_discharge_time) {
//                    String time = etDischargeTime.getText().toString();
//
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("day", Integer.valueOf(time));
//                    Message message = Message.obtain();
//                    message.what = MessageType.MSG_SET_BATTERY_DISCHARGE_DAY;
//                    message.setData(bundle);
//                    sendDJIMessage(message);
//                }
//            }
//            return false;
//        }
//    };

    class LowEnergyListener implements SeekBar.OnSeekBarChangeListener {

        private boolean fromUser = false;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.fromUser = fromUser;
            tvLowEnergy.setText(String.valueOf(progress + MIN_LOW_ENERGY));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            this.fromUser = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (this.fromUser) {
                SharedPreferences sp = getApplicationContext().getSharedPreferences("battery", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                int progress = seekBar.getProgress() + MIN_LOW_ENERGY;

                editor.putInt("lowEnergyWarningThreshold", progress);
                editor.apply();
                tvLowEnergy.setText(String.valueOf(progress));
            }
        }
    }
}
