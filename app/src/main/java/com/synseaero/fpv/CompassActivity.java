package com.synseaero.fpv;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import dji.common.flightcontroller.DJICompassCalibrationStatus;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.sdk.flightcontroller.DJICompass;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJIFlightControllerDelegate;
import dji.sdk.products.DJIAircraft;

public class CompassActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = CompassActivity.class.getName();

    private View btnBack;
    private ImageView ivCompass;
    private TextView tvHint;
    private Button btnCalibrate;


    private DJICompass djiCompass;

    private static final int MSG_CHANGE_FLIGHT_STATUS = 1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_CHANGE_FLIGHT_STATUS:
                    boolean compassError = msg.getData().getBoolean("compassHasError", true);
                    int calibrationStatus = msg.getData().getInt("calibrationStatus", DJICompassCalibrationStatus.Normal.value());
                    boolean isCalibrating = msg.getData().getBoolean("isCalibrating", false);

                    Log.d(TAG, "compassError:" + compassError);
                    Log.d(TAG, "calibrationStatus:" + calibrationStatus);
                    Log.d(TAG, "isCalibrating:" + isCalibrating);

                    //只有收到完成状态就不再接受后续状态变化
                    if (calibrationStatus == DJICompassCalibrationStatus.Succeeded.value()) {
                        ivCompass.setImageResource(R.drawable.compass_horizontal);
                        tvHint.setText(R.string.compass_correct);
                        btnCalibrate.setText(R.string.finish);
                        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
                        if (djiAircraft != null) {
                            DJIFlightController djiFlightController = djiAircraft.getFlightController();
                            djiFlightController.setUpdateSystemStateCallback(null);
                        }
                        break;
                    }

                    //有错误状态
                    if (compassError) {
                        ivCompass.setImageResource(R.drawable.compass_horizontal);
                        tvHint.setText(R.string.compass_incorrect);
                        btnCalibrate.setText(R.string.start_calibrate);
                    }
                    //处于准备状态
                    else if (!isCalibrating) {
                        ivCompass.setImageResource(R.drawable.compass_horizontal);
                        tvHint.setText(R.string.compass_correct);
                        btnCalibrate.setText(R.string.start_calibrate);

                    }
                    //处于校准中状态
                    else {
                        if (calibrationStatus == DJICompassCalibrationStatus.Normal.value()) {
                            ivCompass.setImageResource(R.drawable.compass_horizontal);
                            tvHint.setText(R.string.compass_correct);
                            btnCalibrate.setText(R.string.cancel_calibrate);
                        } else if (calibrationStatus == DJICompassCalibrationStatus.Horizontal.value()) {
                            ivCompass.setImageResource(R.drawable.compass_horizontal);
                            tvHint.setText(R.string.compass_calibrate_horizontal_hint);
                            btnCalibrate.setText(R.string.cancel_calibrate);
                        } else if (calibrationStatus == DJICompassCalibrationStatus.Vertical.value()) {
                            ivCompass.setImageResource(R.drawable.compass_vertical);
                            tvHint.setText(R.string.compass_calibrate_vertical_hint);
                            btnCalibrate.setText(R.string.cancel_calibrate);
                        } else if (calibrationStatus == DJICompassCalibrationStatus.Succeeded.value()) {
                            ivCompass.setImageResource(R.drawable.compass_horizontal);
                            tvHint.setText(R.string.compass_correct);
                            btnCalibrate.setText(R.string.finish);
                        } else if (calibrationStatus == DJICompassCalibrationStatus.Failed.value()) {
                            ivCompass.setImageResource(R.drawable.compass_horizontal);
                            tvHint.setText(R.string.compass_correct);
                            btnCalibrate.setText(R.string.start_calibrate);
                        }
                    }
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止锁屏

        setContentView(R.layout.activity_compass);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);

        ivCompass = (ImageView) findViewById(R.id.iv_aircraft_compass);
        tvHint = (TextView) findViewById(R.id.tv_compass_hint);
        btnCalibrate = (Button) findViewById(R.id.btn_compass_calibrate);
        btnCalibrate.setOnClickListener(this);

        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJIFlightController djiFlightController = djiAircraft.getFlightController();
            djiCompass = djiFlightController.getCompass();
            djiFlightController.setUpdateSystemStateCallback(new FCSystemStateCallback());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_compass_calibrate:
                String text = btnCalibrate.getText().toString();
                if (getText(R.string.start_calibrate).equals(text)) {
                    djiCompass.startCompassCalibration(null);
                } else if (getText(R.string.cancel_calibrate).equals(text)) {
                    djiCompass.stopCompassCalibration(null);
                } else if (getText(R.string.finish).equals(text)) {
                    onBackPressed();
                }
                break;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            DJIFlightController djiFlightController = djiAircraft.getFlightController();
            djiFlightController.setUpdateSystemStateCallback(null);
        }
    }

    class FCSystemStateCallback implements DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback {
        @Override
        public void onResult(DJIFlightControllerCurrentState djiFlightControllerCurrentState) {
            Bundle bundle = new Bundle();
            //bundle.putString("FlightMode", djiFlightControllerCurrentState.getFlightModeString());
            bundle.putBoolean("compassHasError", djiCompass.hasError());
            bundle.putInt("calibrationStatus", djiCompass.getCalibrationStatus().value());
            bundle.putBoolean("isCalibrating", djiCompass.isCalibrating());
            //bundle.putDouble("heading", djiCompass.getHeading());
            Message msg = Message.obtain();
            msg.what = MSG_CHANGE_FLIGHT_STATUS;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

}
