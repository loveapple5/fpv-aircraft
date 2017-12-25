package com.synseaero.fpv.fragment;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.synseaero.fpv.FPVDemoApplication;

import dji.common.battery.DJIBatteryState;
import dji.common.battery.DJIBatteryWarningInformation;
import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.battery.DJIBattery;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;

public class BatterySettingFragment extends Fragment {

    protected static final int MSG_TYPE_BatteryCurrentEnergy = 1;
    protected static final int MSG_TYPE_BatteryTemperature = 2;
    protected static final int MSG_TYPE_BATTERY_STATUS = 3;
    protected static final int MSG_TYPE_BATTERY_SERIAL_NUMBER = 4;
    protected static final int MSG_TYPE_BATTERY_HISTORY = 5;

    private DJIAircraft djiAircraft;
    private DJIFlightController djiFlightController;
    private DJIBattery djiBattery;
    private DJIFlightControllerCurrentState djiFlightControllerCurrentState;
    private int djiRemainingFlightTime;

    private TextView tvBatteryCurrentEnergy;
    private TextView tvBatteryTemperature;
    private TextView tvRemainingFlightTime;

    private EditText etSmartGoHomeThreshold;
    private Button btnSmartGoHomeThreshold;

    private EditText etDischarge;
    private Button btnDischarge;

    private TextView tvBatterySerialNumber;
    private TextView tvDischargeNumber;
    private TextView tvBatteryLifeTime;
    private TextView tvBatteryHistory;

    private EditText etBatteryLowLevel;
    private Button btnBatteryLowLevel;

    private EditText etBatteryVeryLowLevel;
    private Button btnBatteryVeryLowLevel;

    protected Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TYPE_BatteryCurrentEnergy:
                    String BatteryCurrentEnergy = msg.getData().getString("BatteryCurrentEnergy");
                    tvBatteryCurrentEnergy.setText(BatteryCurrentEnergy);
                    break;
                case MSG_TYPE_BatteryTemperature:
                    String BatteryTemperature = msg.getData().getString("BatteryTemperature");
                    tvBatteryTemperature.setText(BatteryTemperature);
                    Log.d("BatteryTemperature", BatteryTemperature);
                    break;
                case MSG_TYPE_BATTERY_STATUS:
                    int dischargeNumber = msg.getData().getInt("dischargeNumber");
                    tvDischargeNumber.setText(dischargeNumber + "次");
                    int batteryLifeTime = msg.getData().getInt("batteryLifeTime");
                    tvBatteryLifeTime.setText(batteryLifeTime + "%");
                    break;
                case MSG_TYPE_BATTERY_SERIAL_NUMBER:
                    String batterySerialNumber = msg.getData().getString("batterySerialNumber");
                    tvBatterySerialNumber.setText(batterySerialNumber);
                    break;
                case MSG_TYPE_BATTERY_HISTORY:
                    tvBatteryHistory.setText(msg.obj.toString());
                    break;
                default:
                    break;
            }
            return false;
        }
    });


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(com.synseaero.fpv.R.layout.fragment_battery_setting, container, false);
        InitUI(view);
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitDJI();
    }

    private void InitUI(View view) {
        tvBatteryCurrentEnergy = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_Battery_Energy);
        tvBatteryTemperature = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_Battery_Temperature);
        tvRemainingFlightTime = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_RemainingFlightTime);
        //是否剩余飞行时间
        djiRemainingFlightTime = djiFlightControllerCurrentState.getSmartGoHomeStatus().getRemainingFlightTime();
        tvRemainingFlightTime.setText(djiRemainingFlightTime + "秒");
        etSmartGoHomeThreshold = (EditText) view.findViewById(com.synseaero.fpv.R.id.et_smart_go_home_threshold);
        btnSmartGoHomeThreshold = (Button) view.findViewById(com.synseaero.fpv.R.id.btn_smart_go_home_threshold);
        btnSmartGoHomeThreshold.setOnClickListener(new SmartGoHomeThresholdListener());
        etDischarge = (EditText) view.findViewById(com.synseaero.fpv.R.id.et_discharge);
        btnDischarge = (Button) view.findViewById(com.synseaero.fpv.R.id.btn_discharge);
        btnDischarge.setOnClickListener(new DischargeTimeClickListener());
        tvBatterySerialNumber = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_battery_serial_number);
        tvDischargeNumber = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_discharge_number);
        tvBatteryLifeTime = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_battery_remaining_life);
        tvBatteryHistory = (TextView) view.findViewById(com.synseaero.fpv.R.id.tv_battery_history);

        //序列号
        djiBattery.getSerialNumber(new DJICommonCallbacks.DJICompletionCallbackWith<String>() {

            @Override
            public void onSuccess(String s) {
                Bundle bundle = new Bundle();
                bundle.putString("batterySerialNumber", s);
                Message msg = Message.obtain();
                msg.what = MSG_TYPE_BATTERY_SERIAL_NUMBER;
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });

        etBatteryLowLevel = (EditText) view.findViewById(com.synseaero.fpv.R.id.tv_battery_low_level);
        btnBatteryLowLevel = (Button) view.findViewById(com.synseaero.fpv.R.id.btn_battery_low_level);
        btnBatteryLowLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strLevel = etBatteryLowLevel.getText().toString();
                if(!strLevel.isEmpty()) {
                    int level = Integer.parseInt(strLevel);
                    djiFlightController.setGoHomeBatteryThreshold(level, null);
                }
            }
        });

        etBatteryVeryLowLevel = (EditText) view.findViewById(com.synseaero.fpv.R.id.tv_battery_very_low_level);
        btnBatteryVeryLowLevel = (Button) view.findViewById(com.synseaero.fpv.R.id.btn_battery_very_low_level);
        btnBatteryVeryLowLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strLevel = etBatteryVeryLowLevel.getText().toString();
                if(!strLevel.isEmpty()) {
                    int level = Integer.parseInt(strLevel);
                    djiFlightController.setLandImmediatelyBatteryThreshold(level, null);
                }

            }
        });
    }

    private void InitDJI() {

        //----------------------------B设置菜单--------------------------------
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        djiFlightController = djiAircraft.getFlightController();
        djiBattery = djiAircraft.getBattery();
        djiFlightControllerCurrentState = djiAircraft.getFlightController().getCurrentState();
        //--------------------------B5智能电池设置------------------------------------
        djiBattery.setBatteryStateUpdateCallback(new BatteryStateUpdateCallback());
//        getCellVoltages单元格电压

//        //严重低电量报警
//        djiFlightControllerCurrentState.getRemainingBattery();//low立即返航verylow立即降落
//        djiBattery.setLevel1CellVoltageThreshold();
//        djiBattery.setLevel2CellVoltageThreshold();//不知道是哪一级的阈值
//        djiBattery.setSelfDischargeDay();//自动放电时间

//        //电池历史信息

        StringBuilder sb = new StringBuilder();
        sb.append(djiRemainingFlightTime);
        sb.append("mAH");

        Bundle bundle = new Bundle();
        bundle.putString("BatteryCurrentEnergy", sb.toString());
        Message msg = Message.obtain();
        msg.what = MSG_TYPE_BatteryCurrentEnergy;
        msg.setData(bundle);
        handler.sendMessage(msg);

    }

    class SmartGoHomeThresholdListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String threshold = etSmartGoHomeThreshold.getText().toString();
            int percent = Integer.parseInt(threshold);
            if (percent > 0 && percent < 100) {
                djiFlightControllerCurrentState.getSmartGoHomeStatus().setBatteryPercentageNeededToGoHome(percent);
            }
        }
    }

    class DischargeTimeClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String strDay = etDischarge.getText().toString();
            int day = Integer.parseInt(strDay);
            djiBattery.setSelfDischargeDay(day, null);
        }
    }

    class BatteryStateUpdateCallback implements DJIBattery.DJIBatteryStateUpdateCallback {
        @Override
        public void onResult(DJIBatteryState djiBatteryState) {

            StringBuilder sb = new StringBuilder();
            sb.append(djiBatteryState.getCurrentEnergy());
            sb.append("mAH");

            Bundle bundle = new Bundle();
            bundle.putString("BatteryCurrentEnergy", sb.toString());
            Message msg = Message.obtain();
            msg.what = MSG_TYPE_BatteryCurrentEnergy;
            msg.setData(bundle);
            handler.sendMessage(msg);

            StringBuilder sb1 = new StringBuilder();
            sb1.append(djiBatteryState.getBatteryTemperature());
            sb1.append("℃");

            Bundle bundle1 = new Bundle();
            bundle1.putString("BatteryTemperature", sb1.toString());
            Message msg1 = Message.obtain();
            msg1.what = MSG_TYPE_BatteryTemperature;
            msg1.setData(bundle1);
            handler.sendMessage(msg1);

            Bundle bundle2 = new Bundle();
            //放电次数
            int dischargeNumber = djiBatteryState.getNumberOfDischarge();
            bundle2.putInt("dischargeNumber", dischargeNumber);
            //电池寿命
            int batteryLifeTime = djiBatteryState.getLifetimeRemainingPercent();
            bundle2.putInt("batteryLifeTime", batteryLifeTime);
            Message msg2 = Message.obtain();
            msg2.what = MSG_TYPE_BATTERY_STATUS;
            msg2.setData(bundle2);
            handler.sendMessage(msg2);

            //低电量警告
            //严重低电量警告
            //int eneryPercent = djiBatteryState.getBatteryEnergyRemainingPercent();
            djiBattery.getWarningInformationRecords(new DJICommonCallbacks.DJICompletionCallbackWith<DJIBatteryWarningInformation[]>() {

                @Override
                public void onSuccess(DJIBatteryWarningInformation[] djiBatteryWarningInformations) {
                    StringBuilder sb = new StringBuilder();
                    if (djiBatteryWarningInformations != null && djiBatteryWarningInformations.length > 0) {
                        for (int i = 0; i < djiBatteryWarningInformations.length; i++) {
                            DJIBatteryWarningInformation information = djiBatteryWarningInformations[i];
                            if (information.hasError()) {
                                sb.append("currentOverload:" + information.isCurrentOverload())
                                        .append(" overHeating:" + information.isOverHeating())
                                        .append(" lowTemperature:" + information.isLowTemperature())
                                        .append(" shortCircuit:" + information.isShortCircuit())
                                        .append(" customDischargeEnabled:" + information.isCustomDischargeEnabled())
                                        .append(" underVoltageBatteryCellIndex:" + information.getUnderVoltageBatteryCellIndex())
                                        .append(" damagedBatteryCellIndex:" + information.getDamagedBatteryCellIndex()).append("\r\n");
                            }
                        }
                    } else {
                        sb.append("正常");
                    }
                    Message msg = Message.obtain();
                    msg.what = MSG_TYPE_BATTERY_HISTORY;
                    msg.obj = sb.toString();
                    handler.sendMessage(msg);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        }
    }
};
