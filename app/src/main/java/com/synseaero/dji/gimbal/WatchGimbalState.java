package com.synseaero.dji.gimbal;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.dji.MessageType;
import com.synseaero.dji.Task;

import dji.common.gimbal.DJIGimbalState;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.gimbal.DJIGimbal;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class WatchGimbalState extends Task {


    public WatchGimbalState(Bundle data, Messenger messenger) {
        super(data, messenger);
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIGimbal gimbal = aircraft.getGimbal();

            if (flag == 0) {
                gimbal.setGimbalStateUpdateCallback(callback);
            } else {
                gimbal.setGimbalStateUpdateCallback(null);
            }

        }
    }

    private DJIGimbal.GimbalStateUpdateCallback callback = new DJIGimbal.GimbalStateUpdateCallback() {

        @Override
        public void onGimbalStateUpdate(DJIGimbal djiGimbal, DJIGimbalState djiGimbalState) {

            //云台电机过载
            boolean isMotorOverloaded = djiGimbalState.isMotorOverloaded();

            Bundle bundle = new Bundle();
            bundle.putBoolean("isMotorOverloaded", isMotorOverloaded);

            Message message = Message.obtain();
            message.what = MessageType.MSG_GET_GIMBAL_STATE_RESPONSE;
            message.setData(bundle);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
}
