package com.synseaero.fpv.dji.flightcontroller;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.fpv.dji.MessageType;
import com.synseaero.fpv.dji.Task;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class GetGoHomeAltitude extends Task {


    public GetGoHomeAltitude(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft)product;
            DJIFlightController flightController = aircraft.getFlightController();
            flightController.getGoHomeAltitude(new DJICommonCallbacks.DJICompletionCallbackWith<Float>() {

                @Override
                public void onSuccess(Float aFloat) {
                    Bundle bundle = new Bundle();
                    bundle.putFloat("goHomeAltitude", aFloat);
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_GO_HOME_ALTITUDE_RESPONSE;
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
                    bundle.putFloat("goHomeAltitude", 0);
                    bundle.putString("DJI_DESC", djiError.getDescription());
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_GO_HOME_ALTITUDE_RESPONSE;
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
