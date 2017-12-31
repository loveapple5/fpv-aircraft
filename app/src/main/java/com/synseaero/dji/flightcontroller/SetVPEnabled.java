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
import dji.sdk.flightcontroller.DJIIntelligentFlightAssistant;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

/***
 * 视觉定位开关
 */
public class SetVPEnabled extends Task {

    private boolean enabled;

    public SetVPEnabled(Bundle data, Messenger messenger) {
        super(data, messenger);
        enabled = data.getBoolean("enabled");
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIFlightController flightController = aircraft.getFlightController();
            DJIIntelligentFlightAssistant assistant = flightController.getIntelligentFlightAssistant();
            if (assistant == null) {
                Bundle bundle = new Bundle();
                bundle.putString("DJI_DESC", "此机型不支持视觉定位");
                Message message = Message.obtain();
                message.what = MessageType.MSG_SET_VP_ENABLED_RESPONSE;
                message.setData(bundle);
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                assistant.setVisionPositioningEnabled(enabled, new DJICommonCallbacks.DJICompletionCallback() {

                    @Override
                    public void onResult(DJIError djiError) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("enabled", enabled);
                        if (djiError != null) {
                            bundle.putString("DJI_DESC", djiError.getDescription());
                        }
                        Message message = Message.obtain();
                        message.what = MessageType.MSG_SET_VP_ENABLED_RESPONSE;
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
}
