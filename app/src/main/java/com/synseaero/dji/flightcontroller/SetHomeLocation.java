package com.synseaero.dji.flightcontroller;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;
import com.synseaero.fpv.R;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class SetHomeLocation extends Task {

    public SetHomeLocation(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIFlightController flightController = aircraft.getFlightController();
            flightController.setHomeLocationUsingAircraftCurrentLocation(new DJICommonCallbacks.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    Bundle bundle = new Bundle();
                    if (djiError != null) {
                        bundle.putString("DJI_DESC", djiError.getDescription());
                    }

                    Message message = Message.obtain();
                    message.what = MessageType.MSG_SET_HOME_LOCATION_RESPONSE;
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
