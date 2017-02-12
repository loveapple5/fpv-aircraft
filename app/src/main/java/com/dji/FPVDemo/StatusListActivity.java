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
import dji.common.error.DJIError;
import dji.common.remotecontroller.DJIRCControlChannel;
import dji.common.remotecontroller.DJIRCControlChannelName;
import dji.common.remotecontroller.DJIRCControlMode;
import dji.common.remotecontroller.DJIRCControlStyle;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.airlink.DJIAirLink;
import dji.sdk.airlink.DJIWiFiLink;
import dji.sdk.battery.DJIBattery;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;

public class StatusListActivity extends Activity {

    protected static final int CHANGE_BATTERY_STATUS = 0;
    protected static final int CHANGE_WIFI_QUALITY = 1;
    protected static final int GET_RC_MODE = 2;

    private DJIBattery djiBattery;
    private DJIAirLink djiAirLink;
    private DJIWiFiLink djiWiFiLink;
    private DJIRemoteController djiRemoteController;

    private TextView tvWifiQuality;
    private TextView tvBatteryStatus;
    //0：中国手 1：美国手 2：日本手 etc
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
        tvBatteryStatus = (TextView) findViewById(R.id.tv_battery_voltage);
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

        DJIAircraft djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        djiRemoteController = djiAircraft.getRemoteController();
        djiRemoteController.getRCControlMode(new GetRCControlModeCallback());
        //djiRemoteController.setRCControlMode(new RCControlMode());
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
