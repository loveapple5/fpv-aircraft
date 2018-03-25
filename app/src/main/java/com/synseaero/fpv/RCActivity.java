package com.synseaero.fpv;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.synseaero.dji.MessageType;
import com.synseaero.view.ActionSheetDialog;

import dji.common.remotecontroller.DJIRCControlStyle;

public class RCActivity extends DJIActivity implements View.OnClickListener {

    private View btnBack;
    private TextView tvWheelSpeed;
    private SeekBar sbWheelSpeed;
    private TextView tcRCMode;
    private TextView ivRCMode1;
    private TextView ivRCMode2;

    //private short wheelSpeed = 50;

    private DJIRCControlStyle rcStyle = DJIRCControlStyle.Chinese;

//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_REMOTE_CONTROLLER_STATUS:
//                    tvWheelSpeed.setText(String.valueOf(wheelSpeed));
//                    sbWheelSpeed.setProgress(wheelSpeed);
//
//                    int modeId = DJIUtils.getMapValue(DJIUtils.rcStyleMap1, rcStyle);
//                    tcRCMode.setText(modeId);
//                    int modeImageId1 = DJIUtils.getMapValue(DJIUtils.rcStyleMap2, rcStyle);
//                    ivRCMode1.setImageResource(modeImageId1);
//                    int modeImageId2 = DJIUtils.getMapValue(DJIUtils.rcStyleMap3, rcStyle);
//                    ivRCMode2.setImageResource(modeImageId2);
//                    break;
//            }
//        }
//    };

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d("RCActivity", "msg:" + msg.what + "");
            int what = msg.what;
            if (what == MessageType.MSG_GET_GIMBAL_WHEEL_SPEED_RESPONSE) {

                Bundle bundle = msg.getData();
                String errDesc = bundle.getString("DJI_DESC", "");
                if (errDesc.isEmpty()) {
                    Short wheelSpeed = bundle.getShort("wheelSpeed");
                    tvWheelSpeed.setText(String.valueOf(wheelSpeed));
                    sbWheelSpeed.setProgress(wheelSpeed);
                } else {
                    Toast.makeText(RCActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                }

            } else if (what == MessageType.MSG_SET_GIMBAL_WHEEL_SPEED_RESPONSE) {

                Bundle bundle = msg.getData();
                String errDesc = bundle.getString("DJI_DESC", "");
                if (errDesc.isEmpty()) {
                    Short wheelSpeed = bundle.getShort("wheelSpeed");
                    tvWheelSpeed.setText(String.valueOf(wheelSpeed));
                    sbWheelSpeed.setProgress(wheelSpeed);

                } else {
                    Toast.makeText(RCActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                }

            } else if (what == MessageType.MSG_GET_REMOTE_CONTROLLER_MODE_RESPONSE) {

                Bundle bundle = msg.getData();
                String errDesc = bundle.getString("DJI_DESC", "");
                if (errDesc.isEmpty()) {
                    int controlStyle = bundle.getInt("controlStyle");
                    int modeId = bundle.getInt("controlStyleResId");
//                    int modeImageId1 = bundle.getInt("controlStyleImageResId1");
//                    int modeImageId2 = bundle.getInt("controlStyleImageResId2");
                    tcRCMode.setText(modeId);
                    ivRCMode1.getBackground().setLevel(controlStyle);
                    ivRCMode2.getBackground().setLevel(controlStyle);
//                    ivRCMode1.setImageResource(modeImageId1);
//                    ivRCMode2.setImageResource(modeImageId2);
                } else {
                    Toast.makeText(RCActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                }

            } else if (what == MessageType.MSG_SET_REMOTE_CONTROLLER_MODE_RESPONSE) {

                Bundle bundle = msg.getData();
                String errDesc = bundle.getString("DJI_DESC", "");
                if (errDesc.isEmpty()) {
                    int controlStyle = bundle.getInt("controlStyle");
                    int modeId = bundle.getInt("controlStyleResId");
//                    int modeImageId1 = bundle.getInt("controlStyleImageResId1");
//                    int modeImageId2 = bundle.getInt("controlStyleImageResId2");
                    tcRCMode.setText(modeId);
                    ivRCMode1.getBackground().setLevel(controlStyle);
                    ivRCMode2.getBackground().setLevel(controlStyle);
//                    ivRCMode1.setImageResource(modeImageId1);
//                    ivRCMode2.setImageResource(modeImageId2);
                } else {
                    Toast.makeText(RCActivity.this, errDesc, Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    private Messenger messenger = new Messenger(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_remote_controller);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);

        tvWheelSpeed = (TextView) findViewById(R.id.tv_gimbal_wheel_speed);
        sbWheelSpeed = (SeekBar) findViewById(R.id.sb_gimbal_wheel_speed);
        tcRCMode = (TextView) findViewById(R.id.tv_rc_mode);
        ivRCMode1 = (TextView) findViewById(R.id.iv_rc_mode_1);
        ivRCMode2 = (TextView) findViewById(R.id.iv_rc_mode_2);


//        DJIAircraft aircraft = FPVDemoApplication.getAircraftInstance();
//        if (aircraft != null) {
//            remoteController = aircraft.getRemoteController();
//            remoteController.getRCWheelControlGimbalSpeed(new DJICommonCallbacks.DJICompletionCallbackWith<Short>() {
//
//                @Override
//                public void onSuccess(Short aShort) {
//                    wheelSpeed = aShort;
//                    handler.sendEmptyMessage(MSG_REMOTE_CONTROLLER_STATUS);
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {
//
//                }
//            });
//            remoteController.getRCControlMode(new DJICommonCallbacks.DJICompletionCallbackWith<DJIRCControlMode>() {
//
//                @Override
//                public void onSuccess(DJIRCControlMode djircControlMode) {
//                    if (DJIUtils.rcStyleMap1.containsKey(djircControlMode.controlStyle)) {
//                        rcStyle = djircControlMode.controlStyle;
//                    }
//                    handler.sendEmptyMessage(MSG_REMOTE_CONTROLLER_STATUS);
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {
//
//                }
//            });
//
//            sbWheelSpeed.setOnSeekBarChangeListener(new WheelSpeedListener());
//            tcRCMode.setOnClickListener(this);
//        }

        sbWheelSpeed.setOnSeekBarChangeListener(new WheelSpeedListener());
        tcRCMode.setOnClickListener(this);

        registerDJIMessenger(MessageType.MSG_GET_GIMBAL_WHEEL_SPEED_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_GIMBAL_WHEEL_SPEED_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_GET_REMOTE_CONTROLLER_MODE_RESPONSE, messenger);
        registerDJIMessenger(MessageType.MSG_SET_REMOTE_CONTROLLER_MODE_RESPONSE, messenger);

        Message message = Message.obtain();
        message.what = MessageType.MSG_GET_GIMBAL_WHEEL_SPEED;
        sendDJIMessage(message);

        Message message2 = Message.obtain();
        message2.what = MessageType.MSG_GET_REMOTE_CONTROLLER_MODE;
        sendDJIMessage(message2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_rc_mode:
                showRCModeMenu();
                break;
        }
    }

    class WheelSpeedListener implements SeekBar.OnSeekBarChangeListener {

        private boolean fromUser = false;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.fromUser = fromUser;
            tvWheelSpeed.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            this.fromUser = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (this.fromUser) {
                Message message = Message.obtain();
                message.what = MessageType.MSG_SET_GIMBAL_WHEEL_SPEED;
                Bundle data = new Bundle();
                data.putShort("wheelSpeed", (short) seekBar.getProgress());
                message.setData(data);
                sendDJIMessage(message);
            }
        }
    }

    private void showRCModeMenu() {

        final int[] strResIds = new int[]{R.string.cn_mode, R.string.us_mode, R.string.jp_mode};

        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {
//                final Object key = DJIUtils.getMapKey(DJIUtils.rcStyleMap1, strResIds[which - 1]);
//                if (key != null && key instanceof DJIRCControlStyle) {
//                    DJIRCControlMode mode = new DJIRCControlMode();
//                    mode.controlStyle = (DJIRCControlStyle) key;
//                    remoteController.setRCControlMode(mode, new DJICommonCallbacks.DJICompletionCallback() {
//
//                        @Override
//                        public void onResult(DJIError djiError) {
//                            if (djiError == null) {
//                                rcStyle = (DJIRCControlStyle) key;
//                                handler.sendEmptyMessage(MSG_REMOTE_CONTROLLER_STATUS);
//                            }
//                        }
//                    });
//
//                }
                Message message = Message.obtain();
                message.what = MessageType.MSG_SET_REMOTE_CONTROLLER_MODE;
                Bundle data = new Bundle();
                data.putInt("controlStyleResId", strResIds[which - 1]);
                message.setData(data);
                sendDJIMessage(message);
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(strResIds[0]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[1]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[2]), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterDJIMessenger(MessageType.MSG_GET_GIMBAL_WHEEL_SPEED_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_GIMBAL_WHEEL_SPEED_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_GET_REMOTE_CONTROLLER_MODE_RESPONSE, messenger);
        unregisterDJIMessenger(MessageType.MSG_SET_REMOTE_CONTROLLER_MODE_RESPONSE, messenger);
    }
}
