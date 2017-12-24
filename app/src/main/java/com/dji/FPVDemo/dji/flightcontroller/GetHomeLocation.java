package com.dji.FPVDemo.dji.flightcontroller;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.dji.FPVDemo.dji.MessageType;
import com.dji.FPVDemo.dji.Task;

import dji.common.error.DJIError;
import dji.common.flightcontroller.DJILocationCoordinate2D;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class GetHomeLocation extends Task {


    public GetHomeLocation(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIFlightController flightController = aircraft.getFlightController();
            flightController.getHomeLocation(new DJICommonCallbacks.DJICompletionCallbackWith<DJILocationCoordinate2D>() {

                @Override
                public void onSuccess(DJILocationCoordinate2D djiLocationCoordinate2D) {
                    Bundle bundle = new Bundle();
                    bundle.putDouble("longitude", djiLocationCoordinate2D.getLongitude());
                    bundle.putDouble("latitude", djiLocationCoordinate2D.getLatitude());
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_HOME_LOCATION_RESPONSE;
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
                    message.what = MessageType.MSG_GET_HOME_LOCATION_RESPONSE;
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
