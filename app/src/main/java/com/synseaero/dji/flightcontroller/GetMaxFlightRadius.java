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
import dji.sdk.flightcontroller.DJIFlightLimitation;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class GetMaxFlightRadius extends Task {

    public GetMaxFlightRadius(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIFlightController flightController = aircraft.getFlightController();
            DJIFlightLimitation flightLimitation = flightController.getFlightLimitation();
            flightLimitation.getMaxFlightRadius(new DJICommonCallbacks.DJICompletionCallbackWith<Float>() {

                @Override
                public void onSuccess(Float radius) {

                    Bundle bundle = new Bundle();
                    bundle.putFloat("radius", radius);

                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_MAX_FLIGHT_RADIUS_RESPONSE;
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
                    message.what = MessageType.MSG_GET_MAX_FLIGHT_RADIUS_RESPONSE;
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
