package com.synseaero.dji.battery;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.battery.DJIBattery;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class GetSeriesNumber extends Task {


    public GetSeriesNumber(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIAircraft djiAircraft = (DJIAircraft) DJISDKManager.getInstance().getDJIProduct();
        if (djiAircraft != null) {
            DJIBattery battery = djiAircraft.getBattery();
            battery.getSerialNumber(new DJICommonCallbacks.DJICompletionCallbackWith<String>() {

                @Override
                public void onSuccess(String batterySerialNumber) {
                    Bundle bundle = new Bundle();
                    bundle.putString("batterySerialNumber", batterySerialNumber);

                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_BATTERY_SERIES_NUMBER_RESPONSE;
                    message.setData(bundle);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {
                    Bundle bundle = new Bundle();
                    bundle.putString("DJI_DESC", djiError.getDescription());
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_BATTERY_SERIES_NUMBER_RESPONSE;
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
