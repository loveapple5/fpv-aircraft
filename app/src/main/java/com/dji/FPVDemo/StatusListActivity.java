package com.dji.FPVDemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dji.FPVDemo.fragment.CompassDialogFragment;

import java.util.ArrayList;
import java.util.List;

import dji.common.airlink.DJIWiFiSignalQuality;
import dji.common.battery.DJIBatteryState;
import dji.common.camera.CameraSDCardState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.DJICompassCalibrationStatus;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.flightcontroller.DJIIMUState;
import dji.common.remotecontroller.DJIRCControlMode;
import dji.common.remotecontroller.DJIRCControlStyle;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJIWiFiLink;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.base.DJIDiagnostics;
import dji.sdk.battery.DJIBattery;
import dji.sdk.camera.DJICamera;
import dji.sdk.flightcontroller.DJICompass;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJIFlightControllerDelegate;
import dji.sdk.gimbal.DJIGimbal;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;

public class StatusListActivity extends FragmentActivity {

    protected static final int MSG_CHANGE_BATTERY_STATUS = 0;
    protected static final int MSG_CHANGE_WIFI_QUALITY = 1;
    protected static final int MSG_GET_RC_MODE = 2;
    protected static final int MSG_CHANGE_FLIGHT_STATUS = 3;
    protected static final int MSG_CHANGE_SDCard_Space = 4;
    protected static final int MSG_GET_GIMBAL_STATES = 5;
    protected static final int MSG_VERSION_STATUS = 6;
    protected static final int MSG_IMU_STAUTS = 7;
    protected static final int MSG_COMPASS_CALIBRATE = 8;

    private static final int CALLBACK_FLIGHT_MODE = 1;
    private static final int CALLBACK_COMPASS_CALIBRATE = 2;
    private int FCCallbackType = CALLBACK_FLIGHT_MODE;

    private DJIAircraft djiAircraft;
    private DJIBattery djiBattery;
    private DJIAirLink djiAirLink;
    private DJIWiFiLink djiWiFiLink;
    private DJIFlightController djiFlightController;
    private DJIRemoteController djiRemoteController;
    private DJICompass djiCompass;
    private DJICamera djiCamera;
    private DJIGimbal djiGimbal;
    private DJIFlightControllerCurrentState djiFlightControllerCurrentState;
    private DJIRCControlMode djircControlMode;

    private TextView tvWifiQuality;
    private TextView tvBatteryStatus;
    private TextView tvFlightMode;
    private TextView tvCompassStatus;
    private TextView tvCompassCalibrate;
    private TextView tvSDCardSpace;
    private Spinner spRCMode;
    private TextView tvGimbalStatus;
    private TextView tvSelfCheck;
    private TextView tvIMUStatus;

    private ArrayList RCModeList;

    protected Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CHANGE_BATTERY_STATUS:
                    String battery = msg.getData().getString("battery");
                    tvBatteryStatus.setText(battery);
                    break;
                case MSG_CHANGE_WIFI_QUALITY:
                    String quality = msg.getData().getString("quality");
                    tvWifiQuality.setText(quality);
                    break;
                case MSG_GET_RC_MODE:
                    String RCMode = msg.getData().getString("RCMode");
                    int position = RCModeList.indexOf(RCMode);
                    spRCMode.setSelection(position);
                    break;
                case MSG_CHANGE_FLIGHT_STATUS:
                    String FlightMode = msg.getData().getString("FlightMode");
                    tvFlightMode.setText(FlightMode);
                    String CompassStatus = msg.getData().getString("isCompassHasError");
                    tvCompassStatus.setText(CompassStatus);
                    String status = msg.getData().getString("CalibrationStatus");
                    boolean isCalibrating = msg.getData().getBoolean("isCalibrating");
                    String Heading = msg.getData().getString("Heading");

                    Log.d("fc", "isCalibrating:" + isCalibrating);
                    Log.d("fc", "status:" + status);
//                    if (status == DJICompassCalibrationStatus.Normal.value()) {
//                        Toast.makeText(StatusListActivity.this, "开始校准", Toast.LENGTH_SHORT).show();
//                    } else if (status == DJICompassCalibrationStatus.Horizontal.value()) {
//                        Toast.makeText(StatusListActivity.this, "水平旋转", Toast.LENGTH_SHORT).show();
//                    } else if (status == DJICompassCalibrationStatus.Vertical.value()) {
//                        Toast.makeText(StatusListActivity.this, "竖直旋转", Toast.LENGTH_SHORT).show();
//                    } else if (status == DJICompassCalibrationStatus.Succeeded.value()) {
//                        Toast.makeText(StatusListActivity.this, "校准完成", Toast.LENGTH_SHORT).show();
//                        djiCompass.stopCompassCalibration(new DJiCompassCalibrateCallback());
//                    } else if(status == DJICompassCalibrationStatus.Failed.value()) {
//                        Toast.makeText(StatusListActivity.this, "校准失败", Toast.LENGTH_SHORT).show();
//                        djiCompass.stopCompassCalibration(new DJiCompassCalibrateCallback());
//                    }
                    tvCompassCalibrate.setText(status);

                    break;
                case MSG_CHANGE_SDCard_Space:
                    String SDCardSpace = msg.getData().getString("SDCardSpace");
                    tvSDCardSpace.setText(SDCardSpace);
                    break;
                case MSG_GET_GIMBAL_STATES:
                    String GimbalStates = msg.getData().getString("GimbalStates");
                    tvGimbalStatus.setText(GimbalStates);
                    break;
                case MSG_VERSION_STATUS:
                    String version = msg.getData().getString("version");
                    tvSelfCheck.setText("有新版本可以升级:" + version);
                    break;
                case MSG_IMU_STAUTS:
                    String IMU = msg.getData().getString("IMU");
                    tvIMUStatus.setText(IMU);
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
        tvWifiQuality = (TextView) findViewById(R.id.tv_wifi_status);
        tvCompassStatus = (TextView) findViewById(R.id.tv_compass_status);
        tvCompassCalibrate = (TextView) findViewById(R.id.tv_compass_calibrate);
        tvFlightMode = (TextView) findViewById(R.id.tv_FlightMode_status);
        tvBatteryStatus = (TextView) findViewById(R.id.tv_battery_voltage);
        tvSDCardSpace = (TextView) findViewById(R.id.tv_sdcard_space);
        spRCMode = (Spinner) findViewById(R.id.sp_RemoteControllerMode);
        tvGimbalStatus = (TextView) findViewById(R.id.tv_gimbal_status);
        tvIMUStatus = (TextView) findViewById(R.id.tv_IMU_status);
        tvSelfCheck = (TextView) findViewById(R.id.tv_self_check_status);

        tvCompassCalibrate.addTextChangedListener(new TextWatcher() {

            private String before;
            private String after;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                before = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                after = editable.toString();
                if (after.compareTo(before) != 0 && DJICompassCalibrationStatus.Normal.toString().compareTo(after) == 0) {
                    Toast.makeText(StatusListActivity.this, "开始校准", Toast.LENGTH_SHORT).show();
                } else if (after.compareTo(before) != 0 && DJICompassCalibrationStatus.Horizontal.toString().compareTo(after) == 0) {
                    Toast.makeText(StatusListActivity.this, "水平旋转", Toast.LENGTH_SHORT).show();
                } else if (after.compareTo(before) != 0 && DJICompassCalibrationStatus.Vertical.toString().compareTo(after) == 0) {
                    Toast.makeText(StatusListActivity.this, "竖直旋转", Toast.LENGTH_SHORT).show();
                } else if (after.compareTo(before) != 0 && DJICompassCalibrationStatus.Succeeded.toString().compareTo(after) == 0) {
                    Toast.makeText(StatusListActivity.this, "校准完成", Toast.LENGTH_SHORT).show();
                    djiCompass.stopCompassCalibration(null);
                } else if (after.compareTo(before) != 0 && DJICompassCalibrationStatus.Failed.toString().equals(after)) {
                    Toast.makeText(StatusListActivity.this, "校准失败", Toast.LENGTH_SHORT).show();
                    djiCompass.stopCompassCalibration(null);
                }
            }
        });

        //数据
        RCModeList = new ArrayList<String>();
        RCModeList.add("日本手");
        RCModeList.add("美国手");
        RCModeList.add("中国手");
//        RCModeList.add("自定义");

        //适配器
        ArrayAdapter RCModeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, RCModeList);
        //设置样式
        RCModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spRCMode.setAdapter(RCModeAdapter);
        spRCMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DJIRCControlStyle style = DJIRCControlStyle.find(i + 1);
                DJIRCControlMode mode = new DJIRCControlMode();
                mode.controlStyle = style;
                djiRemoteController.setRCControlMode(mode, new SetRCControlModeCallback());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvCompassStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("djicompass", djiCompass.getCalibrationStatus().toString());
                CompassDialogFragment compassDialogFragment = new CompassDialogFragment();
                compassDialogFragment.setDialogListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //确定按钮的响应事件
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            //---------------------------B1compass校准-------------------------------
                            djiCompass.startCompassCalibration(new DJiCompassCalibrateCallback());
                        }
                    }
                });
                compassDialogFragment.show(getSupportFragmentManager(), "compass");
            }
        });

    }

    private void initDji() {
        //启动自检
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();

        djiAircraft.setUpdateDiagnosticsListCallback(new DiagnosticsListCallback());
        djiAircraft.getFirmwarePackageVersion();
        djiAirLink = djiAircraft.getAirLink();
        djiWiFiLink = djiAirLink.getWiFiLink();
        djiWiFiLink.setDJIWiFiSignalQualityChangedCallback(new WifiQualityCallback());

        djiBattery = djiAircraft.getBattery();
        djiBattery.setBatteryStateUpdateCallback(new BatteryStateUpdateCallback());


        djiRemoteController = djiAircraft.getRemoteController();
        djiRemoteController.getRCControlMode(new GetRCControlModeCallback());

        djiFlightController = djiAircraft.getFlightController();
        djiCompass = djiFlightController.getCompass();
        djiFlightController.setUpdateSystemStateCallback(new SystemStateCallback());


        djiCamera = djiAircraft.getCamera();
        djiCamera.setDJIUpdateCameraSDCardStateCallBack(new SDCardCallback());

        //djiRemoteController.setRCControlMode(new RCControlMode());

        djiFlightController.setOnIMUStateChangedCallback(new IMUStateChangedCallback());
        djiGimbal = djiAircraft.getGimbal();
        if (djiGimbal != null) {
            tvGimbalStatus.setText("正常");
        } else {
            tvGimbalStatus.setText("错误");
        }

        String version = djiAircraft.getFirmwarePackageVersion();
        tvSelfCheck.setText("当前版本:" + version);

        djiAircraft.setDJIVersionCallback(new VersionChangeCallback());
        //djiGimbal.startGimbalAutoCalibration();
        //djiGimbal.startGimbalBalanceTest();
        //djiGimbal.setGimbalStateUpdateCallback(new GimbalStateCallback());


    }
//    class GimbalStateCallback implements DJIGimbal.GimbalStateUpdateCallback{
//        public void onGimbalStateUpdate(DJIGimbal djiGimbal, DJIGimbalState djiGimbalState){
//
//        };
//    }

    class VersionChangeCallback implements DJIBaseProduct.DJIVersionCallback {

        @Override
        public void onProductVersionChange(String oldVersion, String newVersion) {
            Bundle bundle = new Bundle();
            bundle.putString("version", newVersion);
            Message msg = Message.obtain();
            msg.what = MSG_VERSION_STATUS;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    class DiagnosticsListCallback implements DJIDiagnostics.UpdateDiagnosticsListCallback {
        public void onDiagnosticsListUpdate(List<DJIDiagnostics> djiDiagnosticsList) {
            if (djiDiagnosticsList != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < djiDiagnosticsList.size(); i++) {
                    DJIDiagnostics djiDiagnostics = djiDiagnosticsList.get(i);
                    sb.append(djiDiagnostics.getCode());
                    if (djiDiagnostics.getReason() != null) {
                        sb.append("  " + djiDiagnostics.getReason());
                    }
                    if (djiDiagnostics.getSolution() != null) {
                        sb.append("  " + djiDiagnostics.getSolution() + "\n");
                    }
                }
                Toast.makeText(StatusListActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(StatusListActivity.this, "自检完成", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class BatteryStateUpdateCallback implements DJIBattery.DJIBatteryStateUpdateCallback {
        @Override
        public void onResult(DJIBatteryState djiBatteryState) {
            StringBuffer stringBuffer = new StringBuffer();
            //stringBuffer.delete(0, stringBuffer.length());
            //  djiBatteryState.getNumberOfDischarge()
            stringBuffer.append(djiBatteryState.getBatteryEnergyRemainingPercent()).append("%");
//                stringBuffer.append("CurrentVoltage: ").
//                        append(djiBatteryState.getCurrentVoltage()).append("mV\n");
//                stringBuffer.append("CurrentCurrent: ").
//                        append(djiBatteryState.getCurrentCurrent()).append("mA\n");
            Bundle bundle = new Bundle();
            bundle.putString("battery", stringBuffer.toString());
            Message msg = Message.obtain();
            msg.what = MSG_CHANGE_BATTERY_STATUS;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    class WifiQualityCallback implements DJIWiFiLink.DJIWiFiSignalQualityChangedCallback {

        @Override
        public void onResult(DJIWiFiSignalQuality djiWiFiSignalQuality) {
            int quality = djiWiFiSignalQuality.value();
            StringBuilder sb = new StringBuilder();
            if (quality == DJIWiFiSignalQuality.Good.value()) {
                sb.append("好");
            } else if (quality == DJIWiFiSignalQuality.Medium.value()) {
                sb.append("中");
            } else if (quality == DJIWiFiSignalQuality.bad.value()) {
                sb.append("差");
            } else if (quality == DJIWiFiSignalQuality.Unknown.value()) {
                sb.append("未知");
            }
            Bundle bundle = new Bundle();
            bundle.putString("quality", sb.toString());
            Message msg = Message.obtain();
            msg.what = MSG_CHANGE_WIFI_QUALITY;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    class SystemStateCallback implements DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback {
        @Override
        public void onResult(DJIFlightControllerCurrentState djiFlightControllerCurrentState) {
            // mHomeLatitude = state.getHomeLocation().getLatitude();
            // mHomeLongitude = state.getHomeLocation().getLongitude();
            Bundle bundle = new Bundle();
            bundle.putString("FlightMode", djiFlightControllerCurrentState.getFlightMode().name());
            bundle.putString("isCompassHasError", djiCompass.hasError() ? "指南针错误" : "指南针正常");
            bundle.putString("CalibrationStatus", djiCompass.getCalibrationStatus().toString());
            bundle.putBoolean("isCalibrating", djiCompass.isCalibrating());
            bundle.putDouble("Heading", djiCompass.getHeading());
            Message msg = Message.obtain();
            msg.what = MSG_CHANGE_FLIGHT_STATUS;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    class IMUStateChangedCallback implements DJIFlightControllerDelegate.FlightControllerIMUStateChangedCallback {

        @Override
        public void onStateChanged(DJIIMUState djiimuState) {
            boolean connected = djiimuState.isConnected();
            StringBuilder sb = new StringBuilder();
            Bundle bundle = new Bundle();
            if (connected) {
                sb.append("正常");
            } else {
                sb.append("错误");
            }

            bundle.putString("IMU", sb.toString());
            Message msg = Message.obtain();
            msg.what = MSG_IMU_STAUTS;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    class SDCardCallback implements DJICamera.CameraUpdatedSDCardStateCallback {
        @Override
        public void onResult(CameraSDCardState cameraSDCardState) {
            StringBuilder sb = new StringBuilder();
            Bundle bundle = new Bundle();
            sb.append(cameraSDCardState.getRemainingSpaceInMegaBytes()).append("Mb");
            bundle.putString("SDCardSpace", sb.toString());
            Message msg = Message.obtain();
            msg.what = MSG_CHANGE_SDCard_Space;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    class GetRCControlModeCallback implements DJICommonCallbacks.DJICompletionCallbackWith<DJIRCControlMode> {

        @Override
        public void onSuccess(DJIRCControlMode djircControlMode) {
            int mode = djircControlMode.controlStyle.value();
            StringBuilder sb = new StringBuilder();
            if (mode == DJIRCControlStyle.Japanese.value()) {
                sb.append("日本手");
            } else if (mode == DJIRCControlStyle.Chinese.value()) {
                sb.append("中国手");
            } else if (mode == DJIRCControlStyle.American.value()) {
                sb.append("美国手");
            }


            Bundle bundle = new Bundle();
            bundle.putString("RCMode", sb.toString());
            Message msg = Message.obtain();
            msg.what = MSG_GET_RC_MODE;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onFailure(DJIError djiError) {

        }
    }

    class SetRCControlModeCallback implements DJICommonCallbacks.DJICompletionCallback {

        @Override
        public void onResult(DJIError djiError) {
            if (djiError == null) {
                Toast.makeText(StatusListActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(StatusListActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class DJiCompassCalibrateCallback implements DJICommonCallbacks.DJICompletionCallback {
        @Override
        public void onResult(DJIError djiError) {
        }
    }

}
