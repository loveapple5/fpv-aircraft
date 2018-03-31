package com.synseaero.fpv;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.synseaero.dji.MessageType;
import com.synseaero.view.ActionSheetDialog;
import com.synseaero.view.SwitchButton;


public class FCActivity extends DJIActivity implements View.OnClickListener {

    private EditText etGoHomeAltitude;
    private SwitchButton sbLed;
    private TextView tvFailSafe;
    //private SwitchButton sbVisionPosition;

    private TextView tvSmartGoHomeEnergy;
    private SeekBar sbSmartGoHomeEnergy;

    private TextView tvSmartLandingEnergy;
    private SeekBar sbSmartLandingEnergy;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String errDesc = bundle.getString("DJI_DESC", "");
            if (!errDesc.isEmpty()) {
                Toast.makeText(FCActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                return;
            }
            switch (msg.what) {
                case MessageType.MSG_SET_HOME_LOCATION_RESPONSE: {
                    Toast.makeText(FCActivity.this, R.string.home_location_updated, Toast.LENGTH_SHORT).show();
                    break;
                }
                case MessageType.MSG_GET_GO_HOME_ALTITUDE_RESPONSE:
                case MessageType.MSG_SET_GO_HOME_ALTITUDE_RESPONSE: {
                    float goHomeAltitude = bundle.getFloat("goHomeAltitude");
                    String strAltitude = String.valueOf((int) (goHomeAltitude));
                    etGoHomeAltitude.setText(strAltitude);
                    break;
                }

                case MessageType.MSG_GET_FLIGHT_FAIL_SAFE_OP_RESPONSE:
                case MessageType.MSG_SET_FLIGHT_FAIL_SAFE_OP_RESPONSE: {
                    int operationStrId = bundle.getInt("operationStrId");
                    tvFailSafe.setText(operationStrId);
                    break;
                }
                case MessageType.MSG_GET_GO_HOME_BATTERY_THRESHOLD_RESPONSE:
                case MessageType.MSG_SET_GO_HOME_BATTERY_THRESHOLD_RESPONSE: {
                    int threshold = bundle.getInt("threshold");
                    tvSmartGoHomeEnergy.setText(String.valueOf(threshold));
                    sbSmartGoHomeEnergy.setProgress(threshold);
                    break;
                }
                case MessageType.MSG_GET_LANDING_BATTERY_THRESHOLD_RESPONSE:
                case MessageType.MSG_SET_LANDING_BATTERY_THRESHOLD_RESPONSE: {
                    int threshold = bundle.getInt("threshold");
                    tvSmartLandingEnergy.setText(String.valueOf(threshold));
                    sbSmartLandingEnergy.setProgress(threshold);
                    break;
                }
                case MessageType.MSG_GET_LED_ENABLED_RESPONSE:
                case MessageType.MSG_SET_LED_ENABLED_RESPONSE: {
                    boolean enabled = bundle.getBoolean("enabled");
                    sbLed.setCheckedNoEvent(enabled);
                    break;
                }
//                case MessageType.MSG_GET_VP_ENABLED_RESPONSE:
//                case MessageType.MSG_SET_VP_ENABLED_RESPONSE: {
//                    boolean enabled = bundle.getBoolean("enabled");
//                    sbVisionPosition.setCheckedNoEvent(enabled);
//                    break;
//                }
            }
        }
    };

    private Messenger messenger = new Messenger(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fc);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.ll_flight_limit).setOnClickListener(this);
        findViewById(R.id.tv_go_home).setOnClickListener(this);

        sbLed = (SwitchButton) findViewById(R.id.sb_led);
        sbLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("enabled", isChecked);

                Message message = Message.obtain();
                message.what = MessageType.MSG_SET_LED_ENABLED;
                message.setData(bundle);
                sendDJIMessage(message);
            }
        });

        etGoHomeAltitude = (EditText) findViewById(R.id.et_go_home_height);
        etGoHomeAltitude.setOnEditorActionListener(etListener);
        tvFailSafe = (TextView) findViewById(R.id.tv_fail_safe_operation);
        tvFailSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFailSafeOperationMenu();
            }
        });

//        sbVisionPosition = (SwitchButton) findViewById(R.id.sb_vision_location);
//        sbVisionPosition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                Bundle bundle = new Bundle();
//                bundle.putBoolean("enabled", isChecked);
//
//                Message message = Message.obtain();
//                message.what = MessageType.MSG_SET_VP_ENABLED;
//                message.setData(bundle);
//                sendDJIMessage(message);
//            }
//        });

        tvSmartGoHomeEnergy = (TextView) findViewById(R.id.tv_smart_go_home_energy);
        sbSmartGoHomeEnergy = (SeekBar) findViewById(R.id.sb_smart_go_home_energy);
        sbSmartGoHomeEnergy.setOnSeekBarChangeListener(new SmartGoHomeEnergyListener());

        tvSmartLandingEnergy = (TextView) findViewById(R.id.tv_smart_land_energy);
        sbSmartLandingEnergy = (SeekBar) findViewById(R.id.sb_smart_land_energy);
        sbSmartLandingEnergy.setOnSeekBarChangeListener(new SmartLandingEnergyListener());

        registerDJIMessenger(MessageType.MSG_SET_HOME_LOCATION_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_GO_HOME_ALTITUDE_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_GO_HOME_ALTITUDE_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_FLIGHT_FAIL_SAFE_OP_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_FLIGHT_FAIL_SAFE_OP_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_GO_HOME_BATTERY_THRESHOLD_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_GO_HOME_BATTERY_THRESHOLD_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_LANDING_BATTERY_THRESHOLD_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_LANDING_BATTERY_THRESHOLD_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_LED_ENABLED_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_LED_ENABLED_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_VP_ENABLED_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_VP_ENABLED_RESPONSE, messenger);

        Message getGoHomeHeightMsg = Message.obtain();
        getGoHomeHeightMsg.what = MessageType.MSG_GET_GO_HOME_ALTITUDE;
        sendDJIMessage(getGoHomeHeightMsg);

        Message getFSOPMsg = Message.obtain();
        getFSOPMsg.what = MessageType.MSG_GET_FLIGHT_FAIL_SAFE_OP;
        sendDJIMessage(getFSOPMsg);

        Message getGoHomeThresholdMsg = Message.obtain();
        getGoHomeThresholdMsg.what = MessageType.MSG_GET_GO_HOME_BATTERY_THRESHOLD;
        sendDJIMessage(getGoHomeThresholdMsg);

        Message getLandingThresholdMsg = Message.obtain();
        getLandingThresholdMsg.what = MessageType.MSG_GET_LANDING_BATTERY_THRESHOLD;
        sendDJIMessage(getLandingThresholdMsg);

        Message getLedEnabledMsg = Message.obtain();
        getLedEnabledMsg.what = MessageType.MSG_SET_LED_ENABLED;
        sendDJIMessage(getLedEnabledMsg);

        Message getVPEnabledMsg = Message.obtain();
        getVPEnabledMsg.what = MessageType.MSG_GET_VP_ENABLED;
        sendDJIMessage(getVPEnabledMsg);
        //fetchDJIValue();
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterDJIMessenger(MessageType.MSG_SET_HOME_LOCATION_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_GO_HOME_ALTITUDE_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_GO_HOME_ALTITUDE_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_FLIGHT_FAIL_SAFE_OP_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_FLIGHT_FAIL_SAFE_OP_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_GO_HOME_BATTERY_THRESHOLD_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_GO_HOME_BATTERY_THRESHOLD_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_LANDING_BATTERY_THRESHOLD_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_LANDING_BATTERY_THRESHOLD_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_LED_ENABLED_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_LED_ENABLED_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_VP_ENABLED_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_VP_ENABLED_RESPONSE, messenger);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_flight_limit:
                Intent flIntent = new Intent(this, FLActivity.class);
                startActivity(flIntent);
                break;
            case R.id.tv_go_home:
                Message message = Message.obtain();
                message.what = MessageType.MSG_SET_HOME_LOCATION;
                sendDJIMessage(message);
                break;

        }
    }

    private TextView.OnEditorActionListener etListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //按下确认的时候进行处理
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                int id = v.getId();
                if (id == R.id.et_go_home_height) {
                    String strGoHomeHeight = etGoHomeAltitude.getText().toString();
                    float height = Float.valueOf(strGoHomeHeight);
                    if (height < 20 || height > 500) {
                        Toast.makeText(FCActivity.this, getString(R.string.number_between_a_and_b, 20, 500), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putFloat("goHomeAltitude", height);

                    Message message = Message.obtain();
                    message.what = MessageType.MSG_SET_GO_HOME_ALTITUDE;
                    message.setData(bundle);
                    sendDJIMessage(message);

                }
            }
            return false;
        }
    };

    private void showFailSafeOperationMenu() {

        final int[] strResIds = new int[]{R.string.hover, R.string.landing, R.string.go_home};

        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {

                Bundle bundle = new Bundle();
                bundle.putInt("operationStrId", strResIds[which - 1]);

                Message msg = Message.obtain();
                msg.what = MessageType.MSG_SET_FLIGHT_FAIL_SAFE_OP;
                msg.setData(bundle);
                sendDJIMessage(msg);
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(strResIds[0]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[1]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[2]), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }

    class SmartGoHomeEnergyListener implements SeekBar.OnSeekBarChangeListener {

        private boolean fromUser = false;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.fromUser = fromUser;
            tvSmartGoHomeEnergy.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            this.fromUser = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (this.fromUser) {

                Bundle bundle = new Bundle();
                bundle.putInt("threshold", seekBar.getProgress());

                Message message = Message.obtain();
                message.what = MessageType.MSG_SET_GO_HOME_BATTERY_THRESHOLD;
                message.setData(bundle);
                sendDJIMessage(message);
            }
        }
    }

    class SmartLandingEnergyListener implements SeekBar.OnSeekBarChangeListener {

        private boolean fromUser = false;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.fromUser = fromUser;
            tvSmartLandingEnergy.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            this.fromUser = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (this.fromUser) {

                Bundle bundle = new Bundle();
                bundle.putInt("threshold", seekBar.getProgress());

                Message message = Message.obtain();
                message.what = MessageType.MSG_SET_LANDING_BATTERY_THRESHOLD;
                message.setData(bundle);
                sendDJIMessage(message);
            }
        }
    }
}
