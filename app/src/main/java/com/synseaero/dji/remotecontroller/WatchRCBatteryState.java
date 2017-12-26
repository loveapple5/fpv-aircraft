package com.synseaero.dji.remotecontroller;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.remotecontroller.DJIRCBatteryInfo;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;
import dji.sdk.sdkmanager.DJISDKManager;


public class WatchRCBatteryState extends Task {

    public WatchRCBatteryState(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIRemoteController remoteController = aircraft.getRemoteController();

            if (flag == 0) {
                remoteController.setBatteryStateUpdateCallback(callback);
            } else {
                remoteController.setBatteryStateUpdateCallback(null);
            }

        }
    }

    DJIRemoteController.RCBatteryStateUpdateCallback callback = new DJIRemoteController.RCBatteryStateUpdateCallback() {

        @Override
        public void onBatteryStateUpdate(DJIRemoteController djiRemoteController, DJIRCBatteryInfo batteryInfo) {
            int remainingPercent = batteryInfo.remainingEnergyInPercent;
            int remainingMAh = batteryInfo.remainingEnergyInMAh;


            Bundle bundle = new Bundle();
            bundle.putInt("remainingPercent", remainingPercent);
            bundle.putInt("remainingMAh", remainingMAh);

            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_RC_BATTERY_STATE_RESPONSE;
            message.setData(bundle);
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {

            }

        }
    };
}
