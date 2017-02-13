package com.dji.FPVDemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dji.common.airlink.DJIWiFiSignalQuality;
import dji.common.battery.DJIBatteryState;
import dji.common.camera.CameraSDCardState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.flightcontroller.DJIFlightControllerFlightMode;
import dji.common.flightcontroller.DJIIMUState;
import dji.common.remotecontroller.DJIRCControlChannel;
import dji.common.remotecontroller.DJIRCControlChannelName;
import dji.common.remotecontroller.DJIRCControlMode;
import dji.common.remotecontroller.DJIRCControlStyle;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJIWiFiLink;
import dji.sdk.battery.DJIBattery;
import dji.sdk.camera.DJICamera;
import dji.sdk.flightcontroller.DJICompass;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJIFlightControllerDelegate;
import dji.sdk.missionmanager.DJIMission;
import dji.sdk.missionmanager.DJIMissionManager;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;

public class StatusListActivity extends Activity {

    protected static final int CHANGE_BATTERY_STATUS = 0;
    protected static final int CHANGE_WIFI_QUALITY = 1;
    protected static final int GET_RC_MODE = 2;
    protected static final int CHANGE_FLIGHT_STATUS = 3;
    protected static final int CHANGE_SDCard_Space=4;

    private DJIAircraft djiAircraft;
    private DJIBattery djiBattery;
    private DJIAirLink djiAirLink;
    private DJIWiFiLink djiWiFiLink;
    private DJIFlightController djiFlightController;
    private DJIRemoteController djiRemoteController;
    private DJICompass djiCompass;
    private DJICamera djiCamera;

    private TextView tvWifiQuality;
    private TextView tvBatteryStatus;
    private TextView tvFlightMode;
    private TextView tvCompassStatus;
    private TextView tvSDCardSpace;
    private Spinner spRCMode;

    private ArrayList RCModeList;

    protected Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_BATTERY_STATUS:
                    String battery = msg.getData().getString("battery");
                    tvBatteryStatus.setText(battery);
                    break;
                case CHANGE_WIFI_QUALITY:
                    String quality = msg.getData().getString("quality");
                    tvWifiQuality.setText(quality);
                    break;
                case GET_RC_MODE:
                    String RCMode = msg.getData().getString("RCMode");
                    int position = RCModeList.indexOf(RCMode);
                    spRCMode.setSelection(position);
                    break;
                case CHANGE_FLIGHT_STATUS:
                    String FlightMode = msg.getData().getString("FlightMode");
                    tvFlightMode.setText(FlightMode);
                    String CompassStatus = msg.getData().getString("isCompassHasError");
                    tvCompassStatus.setText(CompassStatus);
                    break;
                case CHANGE_SDCard_Space:
                    String SDCardSpace=msg.getData().getString("SDCardSpace");
                    tvSDCardSpace.setText(SDCardSpace);
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
        tvFlightMode =  (TextView) findViewById(R.id.tv_FlightMode_status);
        tvBatteryStatus = (TextView) findViewById(R.id.tv_battery_voltage);
        tvSDCardSpace=(TextView) findViewById(R.id.tv_sdcard_space);
        spRCMode = (Spinner) findViewById(R.id.sp_RemoteControllerMode);

        //数据
        RCModeList = new ArrayList<String>();
        RCModeList.add("日本手");
        RCModeList.add("美国手");
        RCModeList.add("中国手");
        RCModeList.add("自定义");
        RCModeList.add("SlaveDefault");
        RCModeList.add("SlaveCustom");
        RCModeList.add("未知");

        //适配器
        ArrayAdapter RCModeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, RCModeList);
        //设置样式
        RCModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spRCMode.setAdapter(RCModeAdapter);
        spRCMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DJIRCControlStyle style = DJIRCControlStyle.find(i);
                //DJIRCControlChannel channel[] = new DJIRCControlChannel[4];
//                for(int j = 0; j < 4; j++) {
//                    channel[j] = new DJIRCControlChannel();
//                    channel[j].channel = DJIRCControlChannelName.find(j);
//                }
                DJIRCControlMode mode = new DJIRCControlMode();
                mode.controlStyle = style;
                djiRemoteController.setRCControlMode(mode, new SetRCControlModeCallback());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initDji() {

        //FPVDemoApplication.getProductInstance().setUpdateDiagnosticsListCallback();
        djiAirLink = FPVDemoApplication.getProductInstance().getAirLink();
        djiWiFiLink = djiAirLink.getWiFiLink();
        djiWiFiLink.setDJIWiFiSignalQualityChangedCallback(new WifiQualityCallback());


//        djiWiFiLink.setDJIWiFiGetSignalChangedCallback(new DJIWiFiLink.DJIWiFiGetSignalChangedCallback(){
//            @Override
//            public void onResult(int i) {
//                StringBuffer stringBuffer = new StringBuffer();
//
//            }
//        });

        djiBattery = FPVDemoApplication.getProductInstance().getBattery();
        djiBattery.setBatteryStateUpdateCallback(new BatteryStateUpdateCallback());

        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        djiRemoteController = djiAircraft.getRemoteController();
        djiRemoteController.getRCControlMode(new GetRCControlModeCallback());

        djiFlightController = djiAircraft.getFlightController();
        djiCompass= djiFlightController.getCompass();
        djiFlightController.setUpdateSystemStateCallback(new SystemStateCallback());

        djiCamera=FPVDemoApplication.getProductInstance().getCamera();
        djiCamera.setDJIUpdateCameraSDCardStateCallBack(new SDCardCallback());
        //djiRemoteController.setRCControlMode(new RCControlMode());

        //djiFlightController.setOnIMUStateChangedCallback(new IMUStateChangedCallback());


    }

    class BatteryStateUpdateCallback implements DJIBattery.DJIBatteryStateUpdateCallback {
        @Override
        public void onResult(DJIBatteryState djiBatteryState) {
            StringBuffer stringBuffer = new StringBuffer();
            //stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append(djiBatteryState.getBatteryEnergyRemainingPercent()).append("%");
//                stringBuffer.append("CurrentVoltage: ").
//                        append(djiBatteryState.getCurrentVoltage()).append("mV\n");
//                stringBuffer.append("CurrentCurrent: ").
//                        append(djiBatteryState.getCurrentCurrent()).append("mA\n");
            Bundle bundle = new Bundle();
            bundle.putString("battery", stringBuffer.toString());
            Message msg = Message.obtain();
            msg.what = CHANGE_BATTERY_STATUS;
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
            msg.what = CHANGE_WIFI_QUALITY;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    class SystemStateCallback implements DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback{
        @Override
        public void onResult(DJIFlightControllerCurrentState djiFlightControllerCurrentState) {
           // mHomeLatitude = state.getHomeLocation().getLatitude();
            // mHomeLongitude = state.getHomeLocation().getLongitude();
            StringBuilder sb = new StringBuilder();
            Bundle bundle = new Bundle();
            sb.append(djiFlightControllerCurrentState.getFlightMode().name());
            bundle.putString("FlightMode", sb.toString());

            if (null != djiCompass) {
                sb.delete(0, sb.length());

                //if (djiCompass.isCalibrating()){
                 //   sb.append("已校准");
                //}else {
                //    sb.append("未校准");
                //}

               // bundle.putString("isCompassCalibrating", sb.toString());

                if (djiCompass.hasError()){
                    sb.append("指南针错误");
                }else {
                    sb.append("指南针正常");
                }

                bundle.putString("isCompassHasError", sb.toString());
            }

            Message msg = Message.obtain();
            msg.what =CHANGE_FLIGHT_STATUS;
            msg.setData(bundle);
            handler.sendMessage(msg);

        }
    }

    //public interface IMUStateChangedCallback implements DJIFlightControllerDelegate.FlightControllerIMUStateChangedCallback {

   //     public abstract void onStateChanged(DJIIMUState var1);
    //}

    class SDCardCallback implements DJICamera.CameraUpdatedSDCardStateCallback{
        @Override
        public void onResult(CameraSDCardState cameraSDCardState) {
            StringBuilder sb = new StringBuilder();
            Bundle bundle = new Bundle();
            sb.append(cameraSDCardState.getRemainingSpaceInMegaBytes()).append("Mb");
            bundle.putString("SDCardSpace", sb.toString());
            Message msg = Message.obtain();
            msg.what =CHANGE_SDCard_Space;
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
            } else if (mode == DJIRCControlStyle.Custom.value()) {
                sb.append("自定义");
            } else if (mode == DJIRCControlStyle.SlaveCustom.value()) {
                sb.append("SlaveCustom");
            } else if (mode == DJIRCControlStyle.SlaveDefault.value()) {
                sb.append("SlaveDefault");
            } else if (mode == DJIRCControlStyle.Unknown.value()) {
                sb.append("未知");
            }

            Bundle bundle = new Bundle();
            bundle.putString("RCMode", sb.toString());
            Message msg = Message.obtain();
            msg.what = GET_RC_MODE;
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
            Toast.makeText(StatusListActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
        }
    }
}
