package com.synseaero.dji.battery;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.battery.DJIBattery;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class SetSelfDischargeDay extends Task {

    private int day;

    public SetSelfDischargeDay(Bundle data, Messenger messenger) {
        super(data, messenger);
        day = data.getInt("day");
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIBattery djiBattery = aircraft.getBattery();
            djiBattery.setSelfDischargeDay(day, new DJICommonCallbacks.DJICompletionCallback() {

                @Override
                public void onResult(DJIError djiError) {
                    Bundle bundle = new Bundle();
                    if (djiError != null) {
                        bundle.putString("DJI_DESC", djiError.getDescription());
                    }
                    bundle.putInt("day", day);
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_SET_BATTERY_DISCHARGE_DAY_RESPONSE;
                    message.setData(bundle);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
