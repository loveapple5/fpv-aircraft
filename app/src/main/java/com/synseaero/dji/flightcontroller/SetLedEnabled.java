package com.synseaero.dji.flightcontroller;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class SetLedEnabled extends Task {

    private boolean enabled;

    public SetLedEnabled(Bundle data, Messenger messenger) {
        super(data, messenger);
        enabled = data.getBoolean("enabled");
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIFlightController flightController = aircraft.getFlightController();
            flightController.setLEDsEnabled(enabled, new DJICommonCallbacks.DJICompletionCallback() {

                @Override
                public void onResult(DJIError djiError) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("enabled", enabled);
                    if (djiError != null) {
                        bundle.putString("DJI_DESC", djiError.getDescription());
                    }
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_SET_LED_ENABLED_RESPONSE;
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
