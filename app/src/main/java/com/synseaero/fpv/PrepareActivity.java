package com.synseaero.fpv;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.TextView;

import com.synseaero.dji.MessageType;

public class PrepareActivity extends DJIActivity implements View.OnClickListener {

    private View ivFlyZone;
    private View ivModelCheck;
    private View ivIMU;
    private View ivCompass;
    private View ivWirelessSignal;
    private View ivGimbal;
    private View ivBatteryRemaining;
    private View ivSDCardRemaining;
    private View ivRCMode;
    private View ivFlightMode;

    private TextView tvFlyZone;
    private TextView tvCompass;
    private TextView tvModelCheck;
    private TextView tvWirelessSignal;
    private TextView tvFlightMode;
    private TextView tvRCMode;
    private TextView tvBatteryRemaining;
    private TextView tvSDCardRemaining;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String errDesc = bundle.getString("DJI_DESC", "");
            switch (msg.what) {
                case MessageType.MSG_GET_FLY_FORBID_STATUS_RESPONSE: {
                    int status = bundle.getInt("status");
                    ivFlyZone.setEnabled(status == 0);
//                    tvFlyZone.setText();
                    if (status == 0) {
                        tvFlyZone.setText(R.string.no_restriction_flight);
                    } else if (status == 2 || status == 3) {
                        tvFlyZone.setText(R.string.restrict_flight);
                    } else {
                        tvFlyZone.setText(R.string.forbid_flight);
                    }
                    break;
                }
                case MessageType.MSG_GET_AIRCRAFT_FIRM_VERSION_RESPONSE: {
                    String version = bundle.getString("version");
                    tvModelCheck.setText(version);
                    ivModelCheck.setEnabled(true);
                    break;
                }
                case MessageType.MSG_GET_UP_LINK_SIGNAL_QUALITY_RESPONSE: {
                    int percent = bundle.getInt("percent");
                    ivWirelessSignal.setEnabled(percent > 20);
                    tvWirelessSignal.setText(percent + "%");
                    break;
                }
                case MessageType.MSG_GET_SDCARD_STATE_RESPONSE: {
                    int remainingSpaceMB = bundle.getInt("remainingSpaceMB", 0);
                    tvSDCardRemaining.setText(remainingSpaceMB + "MB");
                    ivSDCardRemaining.setEnabled(true);
                    break;
                }
                case MessageType.MSG_GET_FC_INFO_STATE_RESPONSE: {
                    String flightModeStr = bundle.getString("flightModeStr", "");
                    tvFlightMode.setText(flightModeStr);
                    ivFlightMode.setEnabled(true);
                    break;
                }
                case MessageType.MSG_GET_REMOTE_CONTROLLER_MODE_RESPONSE: {
                    if (errDesc.isEmpty()) {
                        int modeId = bundle.getInt("controlStyleResId");
                        if (modeId != -1) {
                            tvRCMode.setText(modeId);
                            ivRCMode.setEnabled(true);
                        } else {
                            tvRCMode.setText(R.string.unknown);
                            ivRCMode.setEnabled(false);
                        }
                    } else {
                        tvRCMode.setText(R.string.unknown);
                        ivRCMode.setEnabled(false);
                    }

                    break;
                }
                case MessageType.MSG_GET_BATTERY_STATE_RESPONSE: {
                    int percent = bundle.getInt("remainingPercent", 0);
                    tvBatteryRemaining.setText(percent + "%");
                    ivBatteryRemaining.setEnabled(true);
                    break;
                }
            }
        }
    };

    private Messenger messenger = new Messenger(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_prepare);
        findViewById(R.id.btn_connect_helmet).setOnClickListener(this);

        ivFlyZone = findViewById(R.id.iv_no_fly);
        ivModelCheck = findViewById(R.id.iv_model_check);
        ivIMU = findViewById(R.id.iv_imu);
        ivCompass = findViewById(R.id.iv_compass);
        ivWirelessSignal = findViewById(R.id.iv_wireless_signal);
        ivGimbal = findViewById(R.id.iv_gimbal);
        ivSDCardRemaining = findViewById(R.id.iv_sdcard);
        ivFlightMode = findViewById(R.id.iv_flight_mode);
        ivRCMode = findViewById(R.id.iv_rc_mode);
        ivBatteryRemaining = findViewById(R.id.iv_battery_voltage);

        tvFlyZone = (TextView) findViewById(R.id.tv_fly_zone);
        tvModelCheck = (TextView) findViewById(R.id.tv_version);
        tvWirelessSignal = (TextView) findViewById(R.id.tv_wireless_signal);
        tvFlightMode = (TextView) findViewById(R.id.tv_flight_mode);
        tvRCMode = (TextView) findViewById(R.id.tv_rc_mode);
        tvBatteryRemaining = (TextView) findViewById(R.id.tv_battery_voltage);
        tvSDCardRemaining = (TextView) findViewById(R.id.tv_sdcard);
        tvCompass = (TextView) findViewById(R.id.tv_compass);

        tvRCMode.setOnClickListener(this);
        tvCompass.setOnClickListener(this);
        tvSDCardRemaining.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerDJIMessenger(MessageType.MSG_GET_FLY_FORBID_STATUS_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_AIRCRAFT_FIRM_VERSION_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_DIAGNOSTIS_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_SDCARD_STATE_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_UP_LINK_SIGNAL_QUALITY_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_FC_INFO_STATE_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_REMOTE_CONTROLLER_MODE_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_BATTERY_STATE_RESPONSE, messenger);

        sendWatchDJIMessage(MessageType.MSG_WATCH_FLY_FORBID_STATUS, 0);
        sendWatchDJIMessage(MessageType.MSG_WATCH_SDCARD_STATE, 0);
        sendWatchDJIMessage(MessageType.MSG_WATCH_UP_LINK_SIGNAL_QUALITY, 0);
        sendWatchDJIMessage(MessageType.MSG_WATCH_BATTERY_STATE, 0);

        Message modelVersionMsg = Message.obtain();
        modelVersionMsg.what = MessageType.MSG_GET_AIRCRAFT_FIRM_VERSION;
        sendDJIMessage(modelVersionMsg);

        Message fcInfoMsg = Message.obtain();
        fcInfoMsg.what = MessageType.MSG_GET_FC_INFO_STATE;
        sendDJIMessage(fcInfoMsg);

        Message rcModeMsg = Message.obtain();
        rcModeMsg.what = MessageType.MSG_GET_REMOTE_CONTROLLER_MODE;
        sendDJIMessage(rcModeMsg);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterDJIMessenger(MessageType.MSG_GET_FLY_FORBID_STATUS_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_AIRCRAFT_FIRM_VERSION_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_DIAGNOSTIS_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_SDCARD_STATE_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_UP_LINK_SIGNAL_QUALITY_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_FC_INFO_STATE_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_REMOTE_CONTROLLER_MODE_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_BATTERY_STATE_RESPONSE, messenger);

        sendWatchDJIMessage(MessageType.MSG_WATCH_FLY_FORBID_STATUS, 1);
        sendWatchDJIMessage(MessageType.MSG_WATCH_SDCARD_STATE, 1);
        sendWatchDJIMessage(MessageType.MSG_WATCH_UP_LINK_SIGNAL_QUALITY, 1);
        sendWatchDJIMessage(MessageType.MSG_WATCH_BATTERY_STATE, 1);
    }


    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect_helmet: {
                Intent helmetIntent = new Intent(this, BluetoothActivity.class);
                startActivity(helmetIntent);
                finish();
                break;
            }
            case R.id.tv_compass: {
                Intent compassIntent = new Intent(this, CompassActivity.class);
                startActivity(compassIntent);
                break;
            }
            case R.id.tv_rc_mode: {
                Intent RCIntent = new Intent(this, RCActivity.class);
                startActivity(RCIntent);
                break;
            }
            case R.id.tv_sdcard: {

                break;
            }
        }
    }
}
