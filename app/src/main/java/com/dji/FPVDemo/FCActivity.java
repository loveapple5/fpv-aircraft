package com.dji.FPVDemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dji.FPVDemo.util.DJIUtils;
import com.dji.FPVDemo.view.ActionSheetDialog;
import com.dji.FPVDemo.view.SwitchButton;

import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightFailsafeOperation;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJIIntelligentFlightAssistant;
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
    private SwitchButton sbVisionPosition;

    private DJIFlightController flightController;

    private float goHomeAltitude = 0;
    private DJIFlightFailsafeOperation failSafeOperation = DJIFlightFailsafeOperation.Hover;
    private boolean ledSwitch = false;
    private int goHomeBatteryThreshold = 25;
    private boolean visionPosition = false;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FLIGHT_CONTROLLER_STATUS:
                    String strAltitude = String.valueOf((int) (goHomeAltitude));
                    etGoHomeAltitude.setText(strAltitude);
                    sbLed.setChecked(ledSwitch);
                    int operationId = DJIUtils.getMapValue(DJIUtils.failSafeOperationMap, failSafeOperation);
                    tvFailSafe.setText(operationId);
                    etGoHomeThreshold.setText(String.valueOf(goHomeBatteryThreshold));
                    sbVisionPosition.setChecked(visionPosition);
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
        etGoHomeAltitude.setOnEditorActionListener(etListener);
        tvFailSafe = (TextView) findViewById(R.id.tv_fail_safe_operation);
        tvFailSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFailSafeOperationMenu();
            }
        });

        etGoHomeThreshold = (EditText) findViewById(R.id.et_smart_go_home_threshold);
        etGoHomeThreshold.setOnEditorActionListener(etListener);

        sbVisionPosition = (SwitchButton) findViewById(R.id.sb_vision_location);
        sbVisionPosition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (flightController.getIntelligentFlightAssistant() != null) {
                    flightController.getIntelligentFlightAssistant().setVisionPositioningEnabled(isChecked, null);
                    visionPosition = isChecked;
                } else {
                    Toast.makeText(FCActivity.this, R.string.no_vision_position_hint, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                        failSafeOperation = djiFlightFailsafeOperation;
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

            DJIIntelligentFlightAssistant assistant = flightController.getIntelligentFlightAssistant();
            if (assistant != null) {
                assistant.getVisionPositioningEnabled(new DJICommonCallbacks.DJICompletionCallbackWith<Boolean>() {

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        visionPosition = aBoolean;
                        handler.sendEmptyMessage(MSG_FLIGHT_CONTROLLER_STATUS);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }
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
                    flightController.setGoHomeAltitude(Float.valueOf(strGoHomeHeight), null);
                } else if (id == R.id.et_smart_go_home_threshold) {
                    String strGoHomeThreshold = etGoHomeThreshold.getText().toString();
                    int goHomeThreshold = Integer.valueOf(strGoHomeThreshold);
                    if (goHomeThreshold < 25 || goHomeThreshold > 50) {
                        Toast.makeText(FCActivity.this, getString(R.string.number_between_a_and_b, 25, 50), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    flightController.setGoHomeBatteryThreshold(goHomeThreshold, null);
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
                Object key = DJIUtils.getMapKey(DJIUtils.failSafeOperationMap, strResIds[which - 1]);
                if (key != null && key instanceof DJIFlightFailsafeOperation) {
                    DJIFlightFailsafeOperation operation = (DJIFlightFailsafeOperation) key;
                    flightController.setFlightFailsafeOperation(operation, null);

                    failSafeOperation = operation;
                    Message msg = Message.obtain();
                    msg.what = MSG_FLIGHT_CONTROLLER_STATUS;
                    handler.sendMessage(msg);
                }
            }
        };

        new ActionSheetDialog(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem(getString(strResIds[0]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[1]), ActionSheetDialog.SheetItemColor.Black, listener)
                .addSheetItem(getString(strResIds[2]), ActionSheetDialog.SheetItemColor.Black, listener).show();
    }
}
