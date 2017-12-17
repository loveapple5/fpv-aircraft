package com.dji.FPVDemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dji.FPVDemo.util.DJIUtils;
import com.dji.FPVDemo.view.SwitchButton;

import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightFailsafeOperation;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;


public class FCActivity extends FragmentActivity implements View.OnClickListener {

    protected static final int MSG_FLIGHT_CONTROLLER_STATUS = 1;

    private View btnBack;
    private View btnFlightLimit;
    private View btnGoHome;
    private EditText etGoHomeAltitude;
    private SwitchButton sbLed;
    private TextView tvFailSafe;
    private EditText etGoHomeThreshold;
    private SwitchButton sbVisionLocation;

    private DJIFlightController flightController;

    private float goHomeAltitude = 0;
    private DJIFlightFailsafeOperation failSafe = DJIFlightFailsafeOperation.Hover;
    private boolean ledSwitch = false;
    private int goHomeBatteryThreshold = 25;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FLIGHT_CONTROLLER_STATUS:
                    String strAltitude = String.valueOf((int) (goHomeAltitude));
                    etGoHomeAltitude.setText(strAltitude);
                    sbLed.setChecked(ledSwitch);
                    int operationId = DJIUtils.getMapValue(DJIUtils.failSafeOperationMap, failSafe);
                    tvFailSafe.setText(operationId);
                    etGoHomeThreshold.setText(String.valueOf(goHomeBatteryThreshold));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fc);

        btnBack = findViewById(R.id.iv_back);
        btnBack.setOnClickListener(this);

        btnFlightLimit = findViewById(R.id.ll_flight_limit);
        btnFlightLimit.setOnClickListener(this);

        btnGoHome = findViewById(R.id.tv_go_home);
        btnGoHome.setOnClickListener(this);

        sbLed = (SwitchButton) findViewById(R.id.sb_led);
        sbLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flightController.setLEDsEnabled(isChecked, null);
                ledSwitch = isChecked;
            }
        });

        etGoHomeAltitude = (EditText) findViewById(R.id.et_go_home_height);
        tvFailSafe = (TextView) findViewById(R.id.tv_fail_safe_operation);

        etGoHomeThreshold = (EditText) findViewById(R.id.et_smart_go_home_threshold);

        sbVisionLocation = (SwitchButton) findViewById(R.id.sb_vision_location);

        fetchDJIValue();
    }

    private void fetchDJIValue() {
        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        if (djiAircraft != null) {
            flightController = djiAircraft.getFlightController();
            flightController.getGoHomeAltitude(new DJICommonCallbacks.DJICompletionCallbackWith<Float>() {

                @Override
                public void onSuccess(Float aFloat) {
                    goHomeAltitude = aFloat;
                    handler.sendEmptyMessage(MSG_FLIGHT_CONTROLLER_STATUS);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            flightController.getFlightFailsafeOperation(new DJICommonCallbacks.DJICompletionCallbackWith<DJIFlightFailsafeOperation>() {

                @Override
                public void onSuccess(DJIFlightFailsafeOperation djiFlightFailsafeOperation) {
                    if (DJIUtils.failSafeOperationMap.containsKey(djiFlightFailsafeOperation)) {
                        failSafe = djiFlightFailsafeOperation;
                    }
                    handler.sendEmptyMessage(MSG_FLIGHT_CONTROLLER_STATUS);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            flightController.getLEDsEnabled(new DJICommonCallbacks.DJICompletionCallbackWith<Boolean>() {

                @Override
                public void onSuccess(Boolean aBoolean) {
                    ledSwitch = aBoolean;
                    handler.sendEmptyMessage(MSG_FLIGHT_CONTROLLER_STATUS);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            flightController.getGoHomeBatteryThreshold(new DJICommonCallbacks.DJICompletionCallbackWith<Integer>() {

                @Override
                public void onSuccess(Integer integer) {
                    goHomeBatteryThreshold = integer;
                    handler.sendEmptyMessage(MSG_FLIGHT_CONTROLLER_STATUS);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

        }
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
                flightController.setHomeLocationUsingAircraftCurrentLocation(new DJICommonCallbacks.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            Toast.makeText(FCActivity.this, R.string.home_location_updated, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FCActivity.this, djiError.getDescription(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

        }
    }
}
