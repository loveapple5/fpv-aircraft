package com.dji.FPVDemo.dji.remotecontroller;


import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.dji.FPVDemo.dji.MessageType;
import com.dji.FPVDemo.dji.Task;

import dji.common.error.DJIError;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.products.DJIAircraft;
import dji.sdk.remotecontroller.DJIRemoteController;
import dji.sdk.sdkmanager.DJISDKManager;

public class SetWheelSpeed extends Task {

    private short wheelSpeed;

    public SetWheelSpeed(Bundle data, Messenger messenger) {
        super(data, messenger);
        wheelSpeed = data.getShort("wheelSpeed");
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIRemoteController remoteController = aircraft.getRemoteController();
            remoteController.setRCWheelControlGimbalSpeed(wheelSpeed, new DJICommonCallbacks.DJICompletionCallback() {


                @Override
                public void onResult(DJIError djiError) {

                    Bundle bundle = new Bundle();
                    bundle.putShort("wheelSpeed", wheelSpeed);

                    if (djiError != null) {
                        bundle.putString("DJI_DESC", djiError.getDescription());
                    }

                    Message message = Message.obtain();
                    message.what = MessageType.MSG_SET_GIMBAL_WHEEL_SPEED_RESPONSE;
                    message.setData(bundle);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {

                    }
                }
            });
        }
    }
}
