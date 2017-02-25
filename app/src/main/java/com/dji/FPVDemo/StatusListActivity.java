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
import java.util.List;

import dji.common.airlink.DJIWiFiSignalQuality;
import dji.common.battery.DJIBatteryState;
import dji.common.camera.CameraSDCardState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.flightcontroller.DJIFlightControllerFlightMode;
import dji.common.flightcontroller.DJIIMUState;
import dji.common.gimbal.DJIGimbalState;
import dji.common.remotecontroller.DJIRCControlChannel;
import dji.common.remotecontroller.DJIRCControlChannelName;
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
    protected static final int GET_GIMBAL_STATES=5;
    protected static final int VERSION_STATUS = 6;
    protected static final int IMU_STAUTS = 7;

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
                case GET_GIMBAL_STATES:
                    String GimbalStates=msg.getData().getString("GimbalStates");
                    tvGimbalStatus.setText(GimbalStates);
                    break;
                case VERSION_STATUS:
                    String version = msg.getData().getString("version");
                    tvSelfCheck.setText("有新版本可以升级:" + version);
                    break;
                case IMU_STAUTS:
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
        tvFlightMode =  (TextView) findViewById(R.id.tv_FlightMode_status);
        tvBatteryStatus = (TextView) findViewById(R.id.tv_battery_voltage);
        tvSDCardSpace=(TextView) findViewById(R.id.tv_sdcard_space);
        spRCMode = (Spinner) findViewById(R.id.sp_RemoteControllerMode);
        tvGimbalStatus=(TextView) findViewById(R.id.tv_gimbal_status);
        tvIMUStatus = (TextView) findViewById(R.id.tv_IMU_status);
        tvSelfCheck = (TextView) findViewById(R.id.tv_self_check_status);

        //数据
        RCModeList = new ArrayList<String>();
        RCModeList.add("日本手");
        RCModeList.add("美国手");
        RCModeList.add("中国手");
        RCModeList.add("自定义");
//        RCModeList.add("SlaveDefault");
//        RCModeList.add("SlaveCustom");
//        RCModeList.add("未知");

        //适配器
        ArrayAdapter RCModeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, RCModeList);
        //设置样式
        RCModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spRCMode.setAdapter(RCModeAdapter);
//        spRCMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                DJIRCControlStyle style = DJIRCControlStyle.find(i);
//                DJIRCControlChannel channel[] = new DJIRCControlChannel[4];
//                for(int j = 0; j < 4; j++) {
//                    channel[j] = new DJIRCControlChannel();
//                    channel[j].channel = DJIRCControlChannelName.find(j);
//                }
//                DJIRCControlMode mode = new DJIRCControlMode();
//                mode.controlStyle = style;
//                mode.controlChannel = channel;
//                djiRemoteController.setRCControlMode(mode, new SetRCControlModeCallback());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


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
        djiCompass= djiFlightController.getCompass();
        djiFlightController.setUpdateSystemStateCallback(new SystemStateCallback());


        djiCamera= djiAircraft.getCamera();
        djiCamera.setDJIUpdateCameraSDCardStateCallBack(new SDCardCallback());

        //djiRemoteController.setRCControlMode(new RCControlMode());

        djiFlightController.setOnIMUStateChangedCallback(new IMUStateChangedCallback());
        djiGimbal = djiAircraft.getGimbal();
        if(djiGimbal != null) {
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

//        //----------------------------B设置菜单---------------------------------
//        //---------------------------B1compass校准-------------------------------
//        djiCompass.startCompassCalibration(new djiCompletionCallback());
//        //--------------------------B2飞控参数设置
//        djiFlightControllerCurrentState=djiFlightController.getCurrentState();
//        djiFlightControllerCurrentState.getHomeLocation();//返航点位置
//        djiFlightControllerCurrentState.getGoHomeHeight();//返航高度
//        djiFlightController.getFlightLimitation().getMaxFlightHeight();//最大升限
//        djiFlightController.getFlightLimitation().getMaxFlightRadius();//最大飞行半径
//        //最大距离，新手模式
//        djiFlightControllerCurrentState.getFlightMode();
//        djiFlightControllerCurrentState.setFlightMode();//切换飞行模式
//        djiFlightControllerCurrentState.getSmartGoHomeStatus().isAircraftShouldGoHome();//是否需要返航包括低电量返航
//        djiFlightController.setLEDsEnabled();//lED开关
//        //启动视觉定位
//        djiFlightController.getIntelligentFlightAssistant().setVisionPositioningEnabled();
//        //--------------------------B3遥控器设置--------------------------------------
//        //遥控器校准校准什么？
//        djiRemoteController.getRCWheelControlGimbalSpeed();//云台滚轮控制速度X
//        //摇杆模式
//        djiRemoteController.getRCControlMode(new GetRCControlModeCallback());
//        djiRemoteController.setRCControlMode();//设置参数DJIRCControlStyle
//        //-------------------------B4数据连接设置------------------------------------
//        djiAirLink.getWiFiLink().setWiFiSSID();
//        djiAirLink.getWiFiLink().setWiFiPassword();
//        //--------------------------B5智能电池设置------------------------------------
//        djiBattery.setBatteryStateUpdateCallback(new BatteryStateUpdateCallback());
//        //getBatteryTemperature温度getCurrentEnergy 当前电量（mAh）getCellVoltages单元格电压
//        djiFlightController.getCurrentState().getSmartGoHomeStatus().getRemainingFlightTime()//剩余飞行时间
//        //严重低电量报警
//        djiFlightControllerCurrentState.getRemainingBattery()//low立即返航verylow立即降落
//        djiBattery.setLevel1CellVoltageThreshold();
//        djiBattery.setLevel2CellVoltageThreshold();//不知道是哪一级的阈值
//        djiBattery.setSelfDischargeDay();//自动放电时间
//        //电池历史信息
//        djiBattery.getSerialNumber();//序列号
//        //getNumberOfDischarge放电次数getLifetimeRemainingPercent()电池寿命
////--------------------B6云台设置----------------------------------------
//        djiGimbal.getAdvancedSettingsProfile();
//        djiGimbal.setGimbalStateUpdateCallback();
////getControllerSpeedPitch()俯仰灵敏度，getControllerSpeedYaw()航向灵敏度
//        //IMU
//       djiFlightController.setOnIMUStateChangedCallback();
//        djiFlightController.startIMUCalibration();
//        //失控行为
//        djiFlightController.getFlightFailsafeOperation();


        //读取飞行数据找不到





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
            msg.what = VERSION_STATUS;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    class DiagnosticsListCallback implements DJIDiagnostics.UpdateDiagnosticsListCallback{
         public void onDiagnosticsListUpdate(List<DJIDiagnostics> djiDiagnosticsList) {
             if(djiDiagnosticsList != null) {
                 StringBuilder sb = new StringBuilder();
                 for(int i = 0; i< djiDiagnosticsList.size(); i++) {
                     DJIDiagnostics djiDiagnostics = djiDiagnosticsList.get(i);
                     sb.append(djiDiagnostics.getCode());
                     if(djiDiagnostics.getReason() != null) {
                         sb.append("  " + djiDiagnostics.getReason());
                     }
                     if(djiDiagnostics.getSolution() != null) {
                         sb.append("  " + djiDiagnostics.getSolution() + "\n");
                     }
                 }
                 Toast.makeText(StatusListActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
             }else {
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
            msg.what = CHANGE_FLIGHT_STATUS;
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
            if(connected) {
                sb.append("正常");
            } else {
                sb.append("错误");
            }

            bundle.putString("IMU", sb.toString());
            Message msg = Message.obtain();
            msg.what = IMU_STAUTS;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

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
            }
//            else if (mode == DJIRCControlStyle.SlaveCustom.value()) {
//                sb.append("SlaveCustom");
//            } else if (mode == DJIRCControlStyle.SlaveDefault.value()) {
//                sb.append("SlaveDefault");
//            } else if (mode == DJIRCControlStyle.Unknown.value()) {
//                sb.append("未知");
//            }

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
//    class djiCompletionCallback implements DJICommonCallbacks.DJICompletionCallback{
//        @Override
//        public void onResult(DJIError djiError) {
//
//        }
//    }
}
