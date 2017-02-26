package com.dji.FPVDemo.fragment;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.dji.FPVDemo.FPVDemoApplication;
import com.dji.FPVDemo.R;

import dji.common.battery.DJIBatteryState;
import dji.sdk.battery.DJIBattery;
import dji.sdk.products.DJIAircraft;

/**
 * Created by ZXW on 2017/2/26.
 */

public class BatterySettingFragment extends Fragment {

    protected static final int GET_BatteryCurrentEnergy=1;
    protected static final int GET_BatteryTemperature = 2;

    private DJIAircraft djiAircraft;
    private DJIBattery djiBattery;

    private TextView tvBatteryCurrentEnergy;
    private TextView tvBatteryTemperature;

    protected Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GET_BatteryCurrentEnergy:
                    String BatteryCurrentEnergy = msg.getData().getString("BatteryCurrentEnergy");
                    tvBatteryCurrentEnergy.setText(BatteryCurrentEnergy);
                    break;
                case GET_BatteryTemperature:
                    String BatteryTemperature = msg.getData().getString("BatteryTemperature");
                    tvBatteryTemperature.setText(BatteryTemperature);
                    break;
                default:
                    break;
            }
            return false;
        }
    });


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_battery_setting, container, false);
        InitUI(view);
        InitDJI();
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    };

    private void InitUI(View view){
        tvBatteryCurrentEnergy= (TextView)view.findViewById(R.id.tv_Battery_Energy);
        tvBatteryTemperature= (TextView)view.findViewById(R.id.tv_Battery_Temperature);
    }
    private void InitDJI(){

        //----------------------------B设置菜单--------------------------------
        djiAircraft = (DJIAircraft) FPVDemoApplication.getProductInstance();
        djiBattery=djiAircraft.getBattery();
        //--------------------------B5智能电池设置------------------------------------
        djiBattery.setBatteryStateUpdateCallback(new BatteryStateUpdateCallback());
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
    }

    class BatteryStateUpdateCallback implements DJIBattery.DJIBatteryStateUpdateCallback{
        @Override
        public void onResult(DJIBatteryState djiBatteryState) {

            ;

            StringBuilder sb = new StringBuilder();
            sb.append(djiBatteryState.getCurrentEnergy());
            sb.append("mAH");

            Bundle bundle = new Bundle();
            bundle.putString("BatteryCurrentEnergy", sb.toString());
            Message msg = Message.obtain();
            msg.what = GET_BatteryCurrentEnergy;
            msg.setData(bundle);
            handler.sendMessage(msg);

            StringBuilder sb1 = new StringBuilder();
            sb1.append(djiBatteryState.getBatteryTemperature());
            sb1.append("℃");

            Bundle bundle1 = new Bundle();
            bundle1.putString("BatteryTemperature", sb1.toString());
            Message msg1 = Message.obtain();
            msg1.what = GET_BatteryTemperature;
            msg1.setData(bundle1);
            handler.sendMessage(msg1);
        }
    }
};
