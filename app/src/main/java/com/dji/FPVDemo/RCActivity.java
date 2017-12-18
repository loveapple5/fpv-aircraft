package com.dji.FPVDemo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dji.FPVDemo.util.DJIUtils;
import com.dji.FPVDemo.view.ActionSheetDialog;

import dji.common.error.DJIError;
import dji.common.remotecontroller.DJIRCControlMode;
import dji.common.remotecontroller.DJIRCControlStyle;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;

public class RCActivity extends FragmentActivity implements View.OnClickListener {

    protected static final int MSG_REMOTE_CONTROLLER_STATUS = 1;

    private View btnBack;
    private TextView tvWheelSpeed;
    private SeekBar sbWheelSpeed;
    private TextView tcRCMode;
    private ImageView ivRCMode1;
    private ImageView ivRCMode2;

    private DJIRemoteController remoteController;

    private short wheelSpeed = 50;

    private DJIRCControlStyle rcStyle = DJIRCControlStyle.Chinese;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REMOTE_CONTROLLER_STATUS:
                    tvWheelSpeed.setText(String.valueOf(wheelSpeed));
                    sbWheelSpeed.setProgress(wheelSpeed);

                    int modeId = DJIUtils.getMapValue(DJIUtils.rcStyleMap1, rcStyle);
                    tcRCMode.setText(modeId);
                    int modeImageId1 = DJIUtils.getMapValue(DJIUtils.rcStyleMap2, rcStyle);
                    ivRCMode1.setImageResource(modeImageId1);
                    int modeImageId2 = DJIUtils.getMapValue(DJIUtils.rcStyleMap3, rcStyle);
                    ivRCMode2.setImageResource(modeImageId2);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_remote_controller);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);

        tvWheelSpeed = (TextView) findViewById(R.id.tv_gimbal_wheel_speed);
        sbWheelSpeed = (SeekBar) findViewById(R.id.sb_gimbal_wheel_speed);
        tcRCMode = (TextView) findViewById(R.id.tv_rc_mode);
        ivRCMode1 = (ImageView) findViewById(R.id.iv_rc_mode_1);
        ivRCMode2 = (ImageView) findViewById(R.id.iv_rc_mode_2);


        DJIAircraft aircraft = FPVDemoApplication.getAircraftInstance();
        if (aircraft != null) {
            remoteController = aircraft.getRemoteController();
            remoteController.getRCWheelControlGimbalSpeed(new DJICommonCallbacks.DJICompletionCallbackWith<Short>() {

                @Override
                public void onSuccess(Short aShort) {
                    wheelSpeed = aShort;
                    handler.sendEmptyMessage(MSG_REMOTE_CONTROLLER_STATUS);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    handler.sendEmptyMessage(MSG_REMOTE_CONTROLLER_STATUS);
                }
            });
            remoteController.getRCControlMode(new DJICommonCallbacks.DJICompletionCallbackWith<DJIRCControlMode>() {

                @Override
                public void onSuccess(DJIRCControlMode djircControlMode) {
                    if (DJIUtils.rcStyleMap1.containsKey(djircControlMode.controlStyle)) {
                        rcStyle = djircControlMode.controlStyle;
                    }
                    handler.sendEmptyMessage(MSG_REMOTE_CONTROLLER_STATUS);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    handler.sendEmptyMessage(MSG_REMOTE_CONTROLLER_STATUS);
                }
            });

            sbWheelSpeed.setOnSeekBarChangeListener(new WheelSpeedListener());
            tcRCMode.setOnClickListener(this);
        }
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

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            final short progress = (short) seekBar.getProgress();
            remoteController.setRCWheelControlGimbalSpeed(progress, new DJICommonCallbacks.DJICompletionCallback() {

                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        wheelSpeed = progress;
                        handler.sendEmptyMessage(MSG_REMOTE_CONTROLLER_STATUS);
                    }
                }
            });
        }
    }

    private void showRCModeMenu() {

        final int[] strResIds = new int[]{R.string.cn_mode, R.string.us_mode, R.string.jp_mode};

        ActionSheetDialog.OnSheetItemClickListener listener = new ActionSheetDialog.OnSheetItemClickListener() {

            @Override
            public void onClick(TextView v, int which) {
                final Object key = DJIUtils.getMapKey(DJIUtils.rcStyleMap1, strResIds[which - 1]);
                if (key != null && key instanceof DJIRCControlStyle) {
                    DJIRCControlMode mode = new DJIRCControlMode();
                    mode.controlStyle = (DJIRCControlStyle) key;
                    remoteController.setRCControlMode(mode, new DJICommonCallbacks.DJICompletionCallback() {

                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                rcStyle = (DJIRCControlStyle) key;
                                handler.sendEmptyMessage(MSG_REMOTE_CONTROLLER_STATUS);
                            }
                        }
                    });

                }
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(strResIds[0]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[1]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[2]), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }
}
