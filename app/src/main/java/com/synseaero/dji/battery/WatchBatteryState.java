package com.synseaero.dji.battery;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.battery.DJIBatteryLowCellVoltageOperation;
import dji.common.battery.DJIBatteryState;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.battery.DJIBattery;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class WatchBatteryState extends Task {

    public WatchBatteryState(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIBattery djiBattery = aircraft.getBattery();

            if (flag == 0) {
                djiBattery.setBatteryStateUpdateCallback(callback);
            } else {
                djiBattery.setBatteryStateUpdateCallback(null);
            }
            //djiBattery.setLevel1CellVoltageThreshold();
            //djiBattery.setLevel2CellVoltageThreshold();
        }
    }

    DJIBattery.DJIBatteryStateUpdateCallback callback = new DJIBattery.DJIBatteryStateUpdateCallback() {

        @Override
        public void onResult(DJIBatteryState djiBatteryState) {
            //当前电量mAH
            int currentEnergy = djiBatteryState.getCurrentEnergy();
            //剩余电量百分比
            int remainingPercent = djiBatteryState.getBatteryEnergyRemainingPercent();
            //电池温度
            float batteryTemperature = djiBatteryState.getBatteryTemperature();
            //放电次数
            int dischargeNumber = djiBatteryState.getNumberOfDischarge();
            //电池寿命
            int batteryLifeTime = djiBatteryState.getLifetimeRemainingPercent();

            Bundle bundle = new Bundle();
            bundle.putInt("currentEnergy", currentEnergy);
            bundle.putInt("remainingPercent", remainingPercent);
            bundle.putFloat("batteryTemperature", batteryTemperature);

            bundle.putInt("dischargeNumber", dischargeNumber);
            bundle.putInt("batteryLifeTime", batteryLifeTime);

            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_BATTERY_STATE_RESPONSE;
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
}
