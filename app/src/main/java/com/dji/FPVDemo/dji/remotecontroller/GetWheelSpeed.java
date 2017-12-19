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

public class GetWheelSpeed extends Task {


    public GetWheelSpeed(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIRemoteController remoteController = aircraft.getRemoteController();
            remoteController.getRCWheelControlGimbalSpeed(new DJICommonCallbacks.DJICompletionCallbackWith<Short>() {

                @Override
                public void onSuccess(Short wheelSpeed) {
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_GIMBAL_WHEEL_SPEED_RESPONSE;
                    Bundle bundle = new Bundle();
                    bundle.putShort("wheelSpeed", wheelSpeed);
                    message.setData(bundle);
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {

                    }
                }

                @Override
                public void onFailure(DJIError djiError) {
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_GET_GIMBAL_WHEEL_SPEED_RESPONSE;
                    Bundle bundle = new Bundle();
                    bundle.putString("DJI_DESC", djiError.getDescription());
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
