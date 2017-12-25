package com.synseaero.fpv.dji.gimbal;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.synseaero.fpv.dji.MessageType;
import com.synseaero.fpv.dji.Task;

import dji.common.error.DJIError;
import dji.common.gimbal.DJIGimbalAxis;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.gimbal.DJIGimbal;
import dji.sdk.products.DJIAircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class SetGimbalSmoothingOnAxis extends Task {

    private int smoothing;

    public SetGimbalSmoothingOnAxis(Bundle data, Messenger messenger) {
        super(data, messenger);
        smoothing = data.getInt("smoothing");
    }

    @Override
    public void run() {
        DJIBaseProduct product = DJISDKManager.getInstance().getDJIProduct();
        if (product != null && product instanceof DJIAircraft) {
            DJIAircraft aircraft = (DJIAircraft) product;
            DJIGimbal gimbal = aircraft.getGimbal();
            gimbal.setControllerSmoothingOnAxis(DJIGimbalAxis.Pitch, smoothing, new DJICommonCallbacks.DJICompletionCallback() {

                @Override
                public void onResult(DJIError djiError) {
                    Message message = Message.obtain();
                    message.what = MessageType.MSG_SET_SMOOTHING_ON_AXIS_RESPONSE;
                    Bundle bundle = new Bundle();
                    bundle.putInt("smoothing", smoothing);
                    if (djiError != null) {
                        bundle.putString("DJI_DESC", djiError.getDescription());
                    }
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
